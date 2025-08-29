# GitHub Copilot Code Review — Instruções do Projeto

## Contexto

Este repositório é um backend Java + Spring Boot. Utiliza: Maven/Gradle, Spring Web, Spring Data JPA, Spring Validation, Spring Security (JWT/Bearer), Flyway/Liquibase, JUnit + Mockito/Testcontainers, banco relacional (ex.: PostgreSQL), logs estruturados (JSON), observabilidade (Micrometer/Actuator).

**Objetivo do review:**  
Apontar riscos, bugs e inconsistências, sugerir melhorias concretas e links de trechos específicos do PR. Prefira comentários objetivos, com exemplos de correção, priorizados por impacto.

---

## Prioridades de Revisão

1. **Corretude & Bugs**
   - Fluxos nulos, Optional mal usado, exceções não tratadas, concorrência, datas/zonas, conversões numéricas, overflows, uso indevido de coleções mutáveis.
2. **Segurança**
   - Endpoints expostos indevidamente, checagem de autorização, validação de entrada, injeção de dados (SQL/JPQL, SpEL), headers CORS, vazamento de informação em mensagens de erro, criptografia e segredos em código.
3. **Persistência (JPA)**
   - N+1 (ver @EntityGraph/fetch join), paginação obrigatória em findAll públicos, limites em coleções, cascades corretos, equals/hashCode em entidades, transações (@Transactional no boundary certo), migrações alinhadas ao modelo.
4. **Contratos de API**
   - DTOs e validações (@NotBlank, @Size, etc.), códigos HTTP consistentes, idempotência onde aplicável, versionamento de rotas, mensagens de erro padronizadas.
5. **Performance**
   - Uso de streams vs loops, alocação excessiva, conversões repetidas, consultas pesadas sem índices, cache onde fizer sentido.
6. **Manutenibilidade**
   - Separação de camadas (Controller/Service/Repository), nomes claros, complexidade ciclomática, padrões (Command/Strategy, etc.) quando simplificarem, remoção de dead code.
7. **Testes**
   - Cobertura mínima de serviços e regras, testes de repositório com Testcontainers quando a query é complexa, fixtures claros, asserts significativos, happy path e edge cases.
8. **Observabilidade & Logs**
   - Logs no nível correto, sem dados sensíveis, correlation id propagado, métricas/healthchecks adequados.
9. **Infra/Build**
   - Versões estáveis, fail the build em lint/tests, Dockerfile eficiente, CI com etapas (build/test/scan).

---

## Diretrizes de Estilo

- **Null-safety:** Prefira Optional no boundary, evite Optional em campos de entidade; valide entradas cedo.
- **Imutabilidade:** Use records ou classes imutáveis para DTOs; retorne coleções não mutáveis.
- **Camadas:** Controller fino (mapeamento/validação), Service com regras, Repository apenas persistência; evite lógica em controllers/repositories.
- **Exceções:** ControllerAdvice para mapear erros, não vaze stack traces em respostas.
- **Mapeamento:** Use mapeadores explícitos (ex.: MapStruct) para DTO↔Entity.
- **Nomes:** Métodos e variáveis descritivos; evitar abreviações obscuras.

---

## Segurança (detalhes)

- Confirme autorização em cada endpoint sensível (anote o que falta).
- Rejeite entradas malformadas (Bean Validation) e sanitize saídas onde necessário.
- Verifique SQL Injection: em queries dinâmicas, use parâmetros nomeados.
- Segredos via variáveis de ambiente/Secrets; nunca comite chaves.

---

## Persistência (detalhes)

- Aponte N+1 com sugestão concreta (ex.: @EntityGraph ou join fetch).
- Garanta paginação em listagens públicas; não retorne coleções enormes.
- Valide migrações DB compatíveis com as entidades e down migrations quando exigido.

---

## Testes (detalhes)

- Exija testes para regras novas/alteradas; aponte casos-limite ausentes.
- Sugira Testcontainers quando a query ou transação é crítica.
- Não aceite mocks de domínio onde um teste de integração seria simples e confiável.

---

## Observabilidade e Logs

- Logs com contexto (ids, usuário, requestId), nível adequado, sem PII.
- Pontos de métrica/contador para operações críticas; healthchecks expostos via Actuator.

---

## Checklist para o Copilot Review

- Endpoints têm validação e autorização.
- Consultas críticas sem N+1 e com paginação.
- Regras de negócio cobertas por testes.
- Erros padronizados pelo @ControllerAdvice.
- Logs adequados e sem dados sensíveis.
- Código segue as camadas e nomes coerentes.
- Migrações compatíveis com o modelo.

---

## Como comentar

Seja específico e curto: cite arquivos/linhas e ofereça um patch sugerido quando possível.  
Priorize por impacto: comece por bug/segurança, depois desempenho, depois estilo.  
Se algo é aceitável mas pode melhorar, marque como nit.

---

## Exemplos de comentários (templates)

- **N+1:** “Possível N+1 em UserService.findAll. Sugiro @EntityGraph(attributePaths = {"roles"}) no repositório ou join fetch na query JPQL.”
- **Validação:** “Adicionar @NotBlank em CreateUserDTO.name e retornar 400 com mensagem padronizada.”
- **Transação:** “Método transfer() executa múltiplos writes sem @Transactional. Recomendo anotar o service (propagation REQUIRED).”
- **Teste:** “Faltou teste para quando o saldo é insuficiente; sugiro cenário que espera BusinessException.”
- **Segurança:** “Endpoint DELETE /users/{id} não verifica ROLE_ADMIN; adicionar @PreAuthorize correspondente.”
