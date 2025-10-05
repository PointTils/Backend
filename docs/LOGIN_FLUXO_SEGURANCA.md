# Fluxo de Autenticação e Configuração de Segurança - PointTils

## Visão Geral do Sistema de Autenticação

O sistema PointTils utiliza autenticação baseada em **JSON Web Tokens (JWT)** com suporte a **refresh tokens** para garantir segurança e usabilidade.

## Resposta à Pergunta Inicial

**Não, não é necessário nenhum "secret" prévio para obter tokens JWT no sistema atual.**

Os endpoints de autenticação são públicos e acessíveis sem qualquer chave ou segredo prévio. A chave secreta JWT (`JWT_SECRET`) é utilizada apenas **internamente pelo servidor** para assinar e verificar tokens, não sendo exigida dos clientes durante o processo de autenticação.

## Fluxos de Autenticação

### 1. Fluxo de Login (Obtenção Inicial de Tokens)

```mermaid
graph TD
    A[Cliente] --> B[POST /v1/auth/login<br>com email e senha]
    B --> C[Servidor valida credenciais]
    C --> D{Credenciais válidas?}
    D -->|Sim| E[Gera access token + refresh token]
    D -->|Não| F[Retorna erro 401]
    E --> G[Retorna tokens para cliente]
    G --> H[Cliente armazena tokens]
```

**Endpoint:** `POST /v1/auth/login`
- **Acesso:** Público (não requer autenticação)
- **Payload:** `{ "email": "user@example.com", "password": "senha123" }`
- **Resposta:** Access token + Refresh token

### 2. Fluxo de Refresh Token (Renovação de Tokens)

```mermaid
graph TD
    A[Cliente] --> B[POST /v1/auth/refresh<br>com refresh token]
    B --> C[Servidor valida refresh token]
    C --> D{Token válido?}
    D -->|Sim| E[Gera novos access + refresh tokens]
    D -->|Não| F[Retorna erro 401]
    E --> G[Retorna novos tokens]
    G --> H[Cliente atualiza tokens]
```

**Endpoint:** `POST /v1/auth/refresh`
- **Acesso:** Público (não requer autenticação)
- **Payload:** `{ "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }`
- **Resposta:** Novos access token + refresh token

### 3. Fluxo de Acesso a Recursos Protegidos

```mermaid
graph TD
    A[Cliente] --> B[Request com Authorization: Bearer <token>]
    B --> C[JwtAuthenticationFilter valida token]
    C --> D{Token válido?}
    D -->|Sim| E[Acesso permitido]
    D -->|Não| F[Acesso negado - 401]
```

## Configuração de Segurança

### Chaves e Segredos Configurados

| Configuração | Valor Padrão | Descrição |
|-------------|-------------|-----------|
| `JWT_SECRET` | `testandoUmaNovaSenhaMasterComMaisDeTrintaEdoisCaracteres` | Chave secreta para assinatura JWT |
| `JWT_EXPIRATION_TIME` | 900000 ms (15 min) | Tempo de expiração do access token |
| `JWT_REFRESH_EXPIRATION_TIME` | 604800000 ms (7 dias) | Tempo de expiração do refresh token |

### Endpoints Públicos vs Protegidos

#### Endpoints Públicos (Não requerem autenticação)
- `POST /v1/auth/login` - Login de usuário
- `POST /v1/auth/refresh` - Renovação de tokens
- `GET /api/jwt/public` - Teste público
- `GET /v3/api-docs/**` - Documentação Swagger
- `GET /swagger-ui/**` - UI Swagger

#### Endpoints Protegidos (Requerem autenticação)
- **Todos os outros endpoints** exigem token JWT válido no header:
  ```http
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  ```

### Mecanismos de Segurança Implementados

#### 1. Validação de Tokens JWT
- Assinatura verificada usando HMAC-SHA256
- Verificação de expiração
- Validação de formato e estrutura

#### 2. Proteção contra Brute Force
- `LoginAttemptService` bloqueia IPs após múltiplas tentativas falhas
- Limite de tentativas configurável

#### 3. Configurações de CORS
- Origins permitidos: `*` (configurável)
- Métodos HTTP permitidos: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Headers permitidos: Authorization, Content-Type, Accept, Origin, X-Requested-With

#### 4. Headers de Segurança
- CSRF desabilitado (API REST stateless)
- Frame options desabilitados
- Sessões stateless

## Como Consultar os Endpoints

### 1. Obter Tokens Iniciais
```bash
curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@exemplo.com",
    "password": "senha123"
  }'
```

### 2. Renovar Tokens
```bash
curl -X POST http://localhost:8080/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

### 3. Acessar Recurso Protegido
```bash
curl -X GET http://localhost:8080/api/endpoint-protegido \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Considerações de Segurança

### Pontos Fortes
- ✅ Tokens com tempo de vida limitado
- ✅ Refresh tokens com vida útil maior mas ainda limitada
- ✅ Validação robusta de tokens no servidor
- ✅ Proteção contra brute force attacks
- ✅ Configuração adequada de CORS

### Recomendações
- 🔒 Considerar rotatão periódica da chave JWT_SECRET
- 🔒 Implementar revogação de tokens em cenários específicos
- 🔒 Adicionar logging de atividades de autenticação
- 🔒 Considerar uso de HTTPS em produção

## Variáveis de Ambiente Críticas

Para funcionamento correto, configure no ambiente:

```bash
# Chave JWT (mínimo 256 bits recomendado)
JWT_SECRET=super-chave-secreta-com-minimo-32-caracteres

# Tempos de expiração (em milissegundos)
JWT_EXPIRATION_TIME=900000        # 15 minutos
JWT_REFRESH_EXPIRATION_TIME=604800000  # 7 dias
```

## Troubleshooting

### Erro Comum: Token Inválido ou Expirado
- Verifique se o token está no formato correto
- Confirme se o token não expirou
- Valide se a chave JWT_SECRET está configurada corretamente

### Erro Comum: Acesso Negado
- Certifique-se de incluir o header `Authorization: Bearer <token>`
- Verifique se o endpoint não está na lista de endpoints públicos

### Erro Comum: CORS
- Confirme a configuração de CORS no `SecurityConfiguration`
- Verifique se o origin da requisição está permitido

Este documento fornece uma visão completa do sistema de autenticação atual e como interagir com os endpoints de forma segura.
