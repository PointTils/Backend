#!/usr/bin/env python3
"""
Script de teste de carga para a aplicação PointTils
Testa múltiplos endpoints para gerar métricas de desempenho
"""

import requests
import time
import threading
import random
from concurrent.futures import ThreadPoolExecutor
import json

# Configurações
BASE_URL = "http://localhost:8080"
NUM_REQUESTS = 20000  # Aumentado de 100 para 2000
NUM_THREADS = 50     # Aumentado de 10 para 50

# Endpoints para testar (públicos ou que não requerem autenticação)
ENDPOINTS = [
    "/actuator/health",
    "/actuator/info",
    "/actuator/prometheus",
    "/api/specialties",
    "/api/states",
    "/api/parameters"
]

# Headers comuns
HEADERS = {
    "Content-Type": "application/json",
    "User-Agent": "LoadTest/1.0"
}

def make_request(endpoint):
    """Faz uma requisição para um endpoint e retorna o tempo de resposta"""
    url = f"{BASE_URL}{endpoint}"
    start_time = time.time()
    
    try:
        if endpoint == "/actuator/prometheus":
            response = requests.get(url, headers=HEADERS, timeout=10)
        else:
            response = requests.get(url, headers=HEADERS, timeout=10)
        
        end_time = time.time()
        response_time = (end_time - start_time) * 1000  # em ms
        
        return {
            "endpoint": endpoint,
            "status_code": response.status_code,
            "response_time": response_time,
            "success": response.status_code == 200
        }
    except requests.exceptions.RequestException as e:
        end_time = time.time()
        response_time = (end_time - start_time) * 1000
        return {
            "endpoint": endpoint,
            "status_code": 0,
            "response_time": response_time,
            "success": False,
            "error": str(e)
        }

def worker(worker_id):
    """Função executada por cada thread worker"""
    results = []
    for i in range(NUM_REQUESTS // NUM_THREADS):
        endpoint = random.choice(ENDPOINTS)
        result = make_request(endpoint)
        results.append(result)
        
        # Log a cada 10 requisições
        if (i + 1) % 10 == 0:
            print(f"Worker {worker_id}: {i + 1} requisições completadas")
            
        # Pausa mínima entre requisições para teste pesado
        time.sleep(0.01)
    
    return results

def run_load_test():
    """Executa o teste de carga"""
    print(f"Iniciando teste de carga...")
    print(f"URL base: {BASE_URL}")
    print(f"Total de requisições: {NUM_REQUESTS}")
    print(f"Número de threads: {NUM_THREADS}")
    print(f"Endpoints testados: {ENDPOINTS}")
    print("-" * 50)
    
    start_time = time.time()
    
    # Executar teste em múltiplas threads
    with ThreadPoolExecutor(max_workers=NUM_THREADS) as executor:
        futures = [executor.submit(worker, i) for i in range(NUM_THREADS)]
        all_results = []
        
        for future in futures:
            all_results.extend(future.result())
    
    end_time = time.time()
    total_time = end_time - start_time
    
    # Análise dos resultados
    successful_requests = [r for r in all_results if r["success"]]
    failed_requests = [r for r in all_results if not r["success"]]
    
    response_times = [r["response_time"] for r in successful_requests]
    
    if response_times:
        avg_response_time = sum(response_times) / len(response_times)
        max_response_time = max(response_times)
        min_response_time = min(response_times)
    else:
        avg_response_time = max_response_time = min_response_time = 0
    
    # Estatísticas por endpoint
    endpoint_stats = {}
    for endpoint in ENDPOINTS:
        endpoint_results = [r for r in all_results if r["endpoint"] == endpoint]
        if endpoint_results:
            endpoint_success = [r for r in endpoint_results if r["success"]]
            endpoint_response_times = [r["response_time"] for r in endpoint_success]
            
            if endpoint_response_times:
                endpoint_avg = sum(endpoint_response_times) / len(endpoint_response_times)
            else:
                endpoint_avg = 0
                
            endpoint_stats[endpoint] = {
                "total_requests": len(endpoint_results),
                "successful_requests": len(endpoint_success),
                "success_rate": (len(endpoint_success) / len(endpoint_results)) * 100,
                "avg_response_time": endpoint_avg
            }
    
    # Relatório
    print("\n" + "="*50)
    print("RELATÓRIO DO TESTE DE CARGA")
    print("="*50)
    print(f"Tempo total: {total_time:.2f} segundos")
    print(f"Total de requisições: {len(all_results)}")
    print(f"Requisições bem-sucedidas: {len(successful_requests)}")
    print(f"Requisições com falha: {len(failed_requests)}")
    print(f"Taxa de sucesso: {(len(successful_requests) / len(all_results)) * 100:.2f}%")
    print(f"Tempo médio de resposta: {avg_response_time:.2f} ms")
    print(f"Tempo mínimo de resposta: {min_response_time:.2f} ms")
    print(f"Tempo máximo de resposta: {max_response_time:.2f} ms")
    print(f"Requisições por segundo: {len(all_results) / total_time:.2f}")
    
    print("\nESTATÍSTICAS POR ENDPOINT:")
    print("-" * 50)
    for endpoint, stats in endpoint_stats.items():
        print(f"{endpoint}:")
        print(f"  - Total: {stats['total_requests']}")
        print(f"  - Sucesso: {stats['successful_requests']}")
        print(f"  - Taxa de sucesso: {stats['success_rate']:.2f}%")
        print(f"  - Tempo médio: {stats['avg_response_time']:.2f} ms")
        print()
    
    # Salvar resultados em arquivo
    with open("load_test_results.json", "w") as f:
        json.dump({
            "summary": {
                "total_time": total_time,
                "total_requests": len(all_results),
                "successful_requests": len(successful_requests),
                "failed_requests": len(failed_requests),
                "success_rate": (len(successful_requests) / len(all_results)) * 100,
                "avg_response_time": avg_response_time,
                "min_response_time": min_response_time,
                "max_response_time": max_response_time,
                "requests_per_second": len(all_results) / total_time
            },
            "endpoint_stats": endpoint_stats,
            "detailed_results": all_results
        }, f, indent=2)
    
    print(f"Resultados detalhados salvos em: load_test_results.json")

if __name__ == "__main__":
    run_load_test()
