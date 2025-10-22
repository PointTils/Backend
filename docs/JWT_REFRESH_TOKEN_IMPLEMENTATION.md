# Implementação de Refresh Token JWT

Esta documentação descreve a implementação do sistema de refresh token JWT no projeto PointTils.

## Funcionalidades Implementadas

### 1. JwtService Aprimorado
- **generateRefreshToken()**: Gera tokens de refresh com tempo de expiração maior
- **getRefreshExpirationTime()**: Retorna o tempo de expiração do refresh token
- Suporte para configuração de tempo de expiração via propriedades

### 2. Endpoints de Autenticação

#### POST `/api/auth/login`
Realiza login e retorna tokens de acesso

**Request:**
```json
{
  "email": "usuario@exemplo.com",
  "password": "senha123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

#### POST `/api/auth/refresh`
Renova os tokens usando um refresh token válido

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

#### POST `/api/auth/logout`
Realiza logout e invalida o refresh token

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. DTOs Criados

#### RefreshTokenRequestDTO
```java
public class RefreshTokenRequestDTO {
    private String refreshToken;
    // getters e setters
}
```

#### RefreshTokenResponseDTO
```java
public class RefreshTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    // getters, setters e construtores
}
```

## Configuração

### Propriedades Adicionadas

No `application.properties`:
```properties
security.jwt.refresh-expiration-time=${JWT_REFRESH_EXPIRATION_TIME:86400000}
```

### Variáveis de Ambiente

No arquivo `.env`:
```env
JWT_SECRET=your-super-secret-jwt-key-here-must-be-base64-encoded
JWT_EXPIRATION_TIME=3600000        # 1 hora
JWT_REFRESH_EXPIRATION_TIME=86400000 # 24 horas
```

## Fluxo de Autenticação

1. **Login**: Cliente obtém access token e refresh token
2. **Access Token Expira**: Cliente usa refresh token para obter novos tokens
3. **Refresh Token Expira**: Cliente precisa fazer login novamente
4. **Token Inválido**: Retorna erro 401

## Segurança

- Refresh tokens têm tempo de vida maior (24 horas padrão)
- Refresh tokens são invalidados após uso
- Verificação de expiração em ambos os tokens
- Tratamento de erros adequado

## Testes

Testes implementados em `JwtRefreshTokenTest`:
- Geração de access token
- Geração de refresh token  
- Verificação de tokens não expirados
- Testes de integração com Spring Boot

## Como Usar

1. Configure as variáveis de ambiente no arquivo `.env`
2. Execute a aplicação
3. Use os endpoints:
   - `POST /api/jwt-test/token` para obter tokens
   - `POST /api/jwt-test/refresh` para renovar tokens
   - `GET /api/jwt-test/protegido` para testar autenticação
