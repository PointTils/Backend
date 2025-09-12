# Guia: Como Importar a Documentação da API PointTils para o Postman

Este guia explica como obter o arquivo JSON com os endpoints da API PointTils e importá-lo para o Postman para criar uma collection.

## 📋 Pré-requisitos

- [Postman](https://www.postman.com/downloads/) instalado
- Acesso ao projeto PointTils Backend
- Arquivo `pointtils-api-docs.json` disponível

## 🚀 Passo a Passo

### 1. Obter o Arquivo JSON da Documentação

**Métodos para obter o arquivo:**

#### Opção A: Download Direto (se o servidor estiver rodando)
Se a aplicação estiver em execução, você pode acessar:
```
http://localhost:8080/v3/api-docs
```
Ou para formato YAML:
```
http://localhost:8080/v3/api-docs.yaml
```

#### Opção B: Arquivo Local
O arquivo já está disponível no projeto:
```bash
# Navegue até o diretório do projeto
cd /workspaces/Backend

# Verifique se o arquivo existe
ls -la pointtils-api-docs.json
```

### 2. Importar para o Postman

Siga estes passos para importar a documentação:

1. **Abra o Postman**
2. **Clique em "Import"** no canto superior esquerdo
3. **Selecione "File"** e escolha o arquivo `pointtils-api-docs.json`
4. **Clique em "Import"** para confirmar

### 3. Configurar a Collection

Após a importação, configure os seguintes aspectos:

#### Variáveis de Ambiente (Recomendado)
Crie um ambiente no Postman com as variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `baseUrl` | `http://localhost:8080` | URL base da API |
| `authToken` | (deixe vazio) | Token JWT para autenticação |

#### Configuração da Collection
1. **Selecione a collection "Pointtils API"**
2. **Vá para a aba "Variables"**
3. **Configure as variáveis de collection:**
   - `baseUrl`: `{{baseUrl}}`

### 4. Autenticação JWT

A maioria dos endpoints requer autenticação JWT. Siga estes passos:

1. **Execute o endpoint de login:**
   - `POST {{baseUrl}}/v1/auth/login`
   - Body:
     ```json
     {
       "email": "usuario@exemplo.com",
       "password": "senha123"
     }
     ```

2. **Copie o token** da resposta:
   ```json
   {
     "success": true,
     "message": "Login realizado com sucesso",
     "data": {
       "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
       "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     }
   }
   ```

3. **Configure a autenticação:**
   - Vá para a aba "Authorization" da collection
   - Tipo: "Bearer Token"
   - Token: `{{authToken}}`

4. **Defina a variável de ambiente:**
   ```javascript
   // No teste do endpoint de login, adicione:
   pm.environment.set("authToken", pm.response.json().data.accessToken);
   ```

### 5. Testando os Endpoints

#### Endpoints Públicos (Não requerem autenticação):
- `POST /v1/auth/login` - Login de usuário
- `POST /v1/auth/register` - Registro de usuário
- `POST /v1/interpreters/register` - Registro de intérprete
- `POST /v1/deaf-users/register` - Registro de usuário surdo
- `POST /v1/enterprise-users/register` - Registro de empresa
- `GET /v1/states` - Lista de estados brasileiros
- `GET /v1/states/{stateId}/cities` - Cidades por estado

#### Endpoints Protegidos (Requerem autenticação):
- Todos os outros endpoints requerem o header:
  ```
  Authorization: Bearer {{authToken}}
  ```

### 6. Exemplo de Uso

**Login:**
```http
POST {{baseUrl}}/v1/auth/login
Content-Type: application/json

{
  "email": "admin@pointtils.com",
  "password": "admin123"
}
```

**Buscar Especialidades:**
```http
GET {{baseUrl}}/v1/specialties
Authorization: Bearer {{authToken}}
```

### 7. Dicas e Melhores Práticas

1. **Organize por Tags**: A API está organizada por controllers (Auth, Specialty, Deaf, etc.)
2. **Use Environments**: Diferentes ambientes (dev, staging, prod)
3. **Documentação**: Cada endpoint tem summary e description
4. **Test Scripts**: Adicione scripts para testes automatizados
5. **Monitoramento**: Use o Postman Monitor para checks regulares

### 8. Solução de Problemas

**Erro 401 Unauthorized:**
- Verifique se o token JWT é válido
- Execute novamente o login para obter novo token

**Erro 404 Not Found:**
- Verifique se a URL base está correta
- Confirme se o servidor está rodando

**Erro 500 Internal Server Error:**
- Verifique os logs do servidor
- Confirme se o banco de dados está conectado

## 📊 Estrutura da API

A API PointTils inclui os seguintes módulos:

- **Auth Controller**: Autenticação e gerenciamento de sessão
- **Specialty Controller**: Gerenciamento de especialidades
- **Deaf Controller**: Gerenciamento de usuários surdos  
- **Interpreter Controller**: Gerenciamento de intérpretes
- **Enterprise Controller**: Gerenciamento de empresas
- **User Specialty Controller**: Associação de especialidades a usuários
- **State Controller**: Dados de estados e cidades brasileiras
- **JWT Controller**: Geração e teste de tokens JWT

## 🔄 Atualização da Documentação

Se a API for modificada, atualize a documentação:

```bash
# Gerar nova documentação (se estiver rodando)
curl http://localhost:8080/v3/api-docs > pointtils-api-docs.json

# Reimportar no Postman
```

## 📞 Suporte

Em caso de dúvidas ou problemas, consulte:
- Documentação do Postman: https://learning.postman.com/
- Documentação da API PointTils no arquivo JSON
- Logs da aplicação para troubleshooting

---

*Este documento foi gerado em: 12/09/2025*
*Versão da API: 1.0.0*
