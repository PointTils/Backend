# Exemplos de Testes cURL para Endpoints JWT

## 1. Gerar Tokens (Access + Refresh)

```bash
curl -X POST http://localhost:8080/api/jwt-test/token \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

**Resposta esperada:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

## 2. Renovar Tokens usando Refresh Token

```bash
curl -X POST http://localhost:8080/api/jwt-test/refresh \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmM2ExNDIxNy0xMmIzLTRmMjMtOTBhNi1mNWU2NjIxNzA0MTEiLCJpYXQiOjE3NTY0OTk5MzMsImV4cCI6MTc1NjU4NjMzM30._0ySmhy0_-Dbw9BNSiHvSchkxbgxdMYOX4MueTtGvUA"
  }'
```

**Exemplo com token real:**
```bash
curl -X POST http://localhost:8080/api/jwt-test/refresh \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5YzJlMzQwZC0wZmM0LTQ0Y2ItODAwYS01MjU0ZWM2MjZmMjgiLCJpYXQiOjE3MzkwMjM0MDAsImV4cCI6MTczOTEwOTgwMH0.abcdef1234567890"
  }'
```

## 3. Acessar Endpoint Público (sem autenticação)

```bash
curl -X GET http://localhost:8080/api/jwt-test/publico \
  -H "Accept: application/json"
```

**Resposta esperada:**
```json
"Esse endpoint é público."
```

## 4. Acessar Endpoint Protegido (com autenticação)

```bash
curl -X GET http://localhost:8080/api/jwt-test/protegido \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZjk1Y2Y3ZS1mY2IzLTRkYTAtOGUwMS1kYjM3NGQwNjA2MjQiLCJpYXQiOjE3NTY0OTk5OTIsImV4cCI6MTc1NjUwMzU5Mn0.olhJVUEAVV1bZMCCiQVI4STETkFZfWxb0VvBNzz47hs" \
  -H "Accept: application/json"
```

**Exemplo com token real:**
```bash
curl -X GET http://localhost:8080/api/jwt-test/protegido \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5YzJlMzQwZC0wZmM0LTQ0Y2ItODAwYS01MjU0ZWM2MjZmMjgiLCJpYXQiOjE3MzkwMjM0MDAsImV4cCI6MTczOTAyNzAwMH0.abcdef1234567890" \
  -H "Accept: application/json"
```

## 5. Fluxo Completo de Teste

### Passo 1: Gerar tokens
```bash
RESPONSE=$(curl -s -X POST http://localhost:8080/api/jwt-test/token \
  -H "Content-Type: application/json" \
  -H "Accept: application/json")

echo $RESPONSE
```

### Passo 2: Extrair tokens da resposta
```bash
ACCESS_TOKEN=$(echo $RESPONSE | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
REFRESH_TOKEN=$(echo $RESPONSE | grep -o '"refreshToken":"[^"]*' | cut -d'"' -f4)

echo "Access Token: $ACCESS_TOKEN"
echo "Refresh Token: $REFRESH_TOKEN"
```

### Passo 3: Testar endpoint protegido
```bash
curl -X GET http://localhost:8080/api/jwt-test/protegido \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1MWJlMjYyYS1hYjE1LTRjM2ItOWY0YS0wYzk5ZWVjODdiMGMiLCJpYXQiOjE3NTY1MDAwOTUsImV4cCI6MTc1NjUwMzY5NX0.eW-1V9VxcRUFmnClNYbV7cYByZmf1hAckSomD_VheYk" \
  -H "Accept: application/json"
```

### Passo 4: Renovar tokens
```bash
curl -X POST http://localhost:8080/api/jwt-test/refresh \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "{
    \"refreshToken\": \"$REFRESH_TOKEN\"
  }"
```

## 6. Testar Token Expirado (para verificação de erro)

```bash
# Usar um token expirado manualmente
curl -X GET http://localhost:8080/api/jwt-test/protegido \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNjAwMDAwMDAwLCJpYXQiOjE2MDAwMDAwMDB9.invalid_token_here" \
  -H "Accept: application/json"
```

**Resposta esperada para token expirado/inválido:**
```json
"Token expired" (status 401)
```

## 7. Testar Refresh Token Inválido

```bash
curl -X POST http://localhost:8080/api/jwt-test/refresh \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "refreshToken": "token_invalido_aqui"
  }'
```

**Resposta esperada:**
```json
"Refresh token inválido" (status 401)
```

## Variáveis de Ambiente para Testes

Para facilitar os testes, você pode configurar variáveis:

```bash
# Configurar URL base
export API_URL="http://localhost:8080"

# Após gerar tokens
export ACCESS_TOKEN="seu_access_token"
export REFRESH_TOKEN="seu_refresh_token"

# Usar nas requisições
curl -X GET $API_URL/api/jwt-test/protegido \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

## Dicas de Troubleshooting

1. **Verifique se a aplicação está rodando:**
   ```bash
   curl -I http://localhost:8080/api/jwt-test/publico
   ```

2. **Verifique erros de CORS:**
   - A API está configurada para aceitar requests de `http://localhost:3333`

3. **Verifique o formato do token:**
   - Deve começar com "Bearer " no header Authorization

4. **Teste com ferramentas gráficas:**
