# PointTils Backend

## Visão Geral
Backend desenvolvido em Java Spring Boot para gerenciamento de End-points. Segue uma arquitetura limpa com separação clara de camadas.

## Arquitetura e Padrões

### Organização do repositório
O projeto Java Spring Boot do repositório de Backend está organizado seguindo os princípios de **Clean Architecture**. Esta abordagem envolve a separação clara de responsabilidades em camadas bem definidas, onde cada camada tem uma responsabilidade específica e dependências direcionais bem estabelecidas. Dessa forma, temos uma arquitetura que isola a lógica de negócio das preocupações técnicas, permitindo maior testabilidade, manutenibilidade e flexibilidade.

As camadas seguem o fluxo de dependência de fora para dentro, onde as camadas externas dependem das internas, mas nunca o contrário. Os controllers recebem as requisições HTTP e delegam para os services, que contêm a lógica de negócio e utilizam repositories para acesso aos dados, que por sua vez trabalham com as entities do domínio.

Diante disso, os pacotes do projeto estão divididos da forma abaixo:

📁 **application/**: Lógica de aplicação e casos de uso
- 📁 **controllers/**: Controladores REST que expõem endpoints HTTP
- 📁 **dto/**: Objetos de Transferência de Dados para comunicação externa
- 📁 **mapper/**: Mapeadores para conversão entre DTOs e Entities
- 📁 **services/**: Serviços de negócio com lógica de aplicação

📁 **core/**: Núcleo do domínio com regras de negócio
- 📁 **domain/**: Entidades e enums do domínio
  - 📁 **entities/**: Entidades JPA do domínio

📁 **infrastructure/**: Infraestrutura e configurações técnicas
- 📁 **configs/**: Configurações do Spring Boot (OpenAPI, Exception Handlers)
- 📁 **repositories/**: Interfaces JPA Repository para acesso a dados

📁 **resources/**: Arquivos de configuração
- 📄 **application.properties**: Configurações da aplicação

📁 **test/**: Testes unitários e de integração organizados pela mesma estrutura de packages

### Fluxo Arquitetural
```
┌─────────────────────────────────────────────────┐
│                 Cliente HTTP                    │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│              Controllers (REST)                 │
│ @RestController | @RequestMapping               │
│ - PointController                               │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                 Services                        │
│ @Service | @Transactional                      │
│ - PointService                                  │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│               Repositories                      │
│ @Repository | JpaRepository                     │
│ - PointRepository                               │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                  Domain                         │
│ @Entity | @Table                                │
│ - Point, PointType                              │
└─────────────────────────────────────────────────┘
```

### Padrões de Código
Com intuito de manter legibilidade e consistência no código, as seguintes padronizações foram definidas para o backend Java Spring Boot.

#### Tipagem e Annotations Java
- Sempre utilize annotations do Spring Framework apropriadas (`@Service`, `@Repository`, `@Controller`, `@Entity`)
- Use Lombok para reduzir código boilerplate (`@Data`, `@Builder`, `@RequiredArgsConstructor`)
- Utilize DTOs para todas as comunicações externas, evitando exposição direta das entities
- Implemente validações com Bean Validation quando necessário (`@Valid`, `@NotNull`)

#### Nomenclatura de Classes
Classes do projeto devem ser nomeadas em inglês e seguir o padrão **PascalCase**, ou seja, devem iniciar com letra maiúscula e cada palavra deve iniciar com letra maiúscula. Além disso, devem seguir sufixos específicos conforme sua responsabilidade:

**Controllers:** `{Entidade}Controller.java` ✔️
```java
PointController.java ✔️
Point_Controller.java ❌
pointController.java ❌
ControllerPoint.java ❌
```

**Services:** `{Entidade}Service.java` ✔️
```java
PointService.java ✔️
Point_Service.java ❌
PointServices.java ❌
```

**Repositories:** `{Entidade}Repository.java` ✔️
```java
PointRepository.java ✔️
PointRepo.java ❌
RepositoryPoint.java ❌
```

**DTOs:** `{Entidade}{Tipo}DTO.java` ✔️
```java
PointRequestDTO.java ✔️
PointResponseDTO.java ✔️
PointDto.java ❌
DtoPoint.java ❌
```

**Entities:** `{Entidade}.java` ✔️
```java
Point.java ✔️
PointEntity.java ❌
point.java ❌
```

#### Nomenclatura de Métodos e Variáveis
Métodos e variáveis do projeto devem ser nomeados em inglês e seguir o padrão **camelCase**, ou seja, devem iniciar com letra minúscula e cada palavra subsequente deve iniciar com letra maiúscula:

```java
// Variáveis
private String userId; ✔️
private String user_id; ❌
private String UserId; ❌
private String userid; ❌

// Métodos
public PointResponseDTO findById(Long id) ✔️
public PointResponseDTO FindById(Long id) ❌
public PointResponseDTO find_by_id(Long id) ❌
public PointResponseDTO findbyid(Long id) ❌
```

#### Nomenclatura de Packages
Packages devem seguir o padrão **snake_case** (minúsculas com underscores) e estar organizados por funcionalidade:

```java
com.pointtils.pointtils.src.application.controllers ✔️
com.pointtils.pointtils.src.Application.Controllers ❌
com.pointtils.pointtils.src.applicationControllers ❌
```

#### Nomenclatura de Endpoints REST
Endpoints devem seguir convenções REST com recursos no plural e em inglês:

```java
@RequestMapping("/api/points") ✔️
@GetMapping("/{id}") // GET /api/points/1 ✔️
@PostMapping // POST /api/points ✔️
@PutMapping("/{id}") // PUT /api/points/1 ✔️
@DeleteMapping("/{id}") // DELETE /api/points/1 ✔️

@RequestMapping("/api/point") ❌
@RequestMapping("/api/pontos") ❌
@GetMapping("/getPoint/{id}") ❌
```

### Documentação de Classes e Métodos
Para que outros desenvolvedores possam compreender rapidamente como utilizar as classes e métodos, é importante seguir padrões de documentação. Embora o nome da classe/método muitas vezes seja autoexplicativo, uma documentação clara evita ambiguidades e reduz o tempo necessário para entender o código.

#### Padrão de Documentação para Controllers
```java
/**
 * Controller responsável pela gestão de pontos de trabalho.
 * 
 * Endpoints disponíveis:
 * - GET /api/points - Lista todos os pontos
 * - GET /api/points/{id} - Busca ponto por ID
 * - POST /api/points - Cria novo ponto
 * - PUT /api/points/{id} - Atualiza ponto existente
 * - DELETE /api/points/{id} - Remove ponto
 * 
 * @author Equipe PointTils
 */
@RestController
@RequestMapping("/api/points")
public class PointController {
    // implementação
}
```

#### Padrão de Documentação para Services
```java
/**
 * Serviço responsável pela lógica de negócio relacionada aos pontos de trabalho.
 * 
 * Gerencia:
 * - Validação de regras de negócio
 * - Transformação de dados entre DTOs e Entities
 * - Transações de banco de dados
 * 
 * @param pointRepository - Repository para acesso aos dados de Point
 * @param pointMapper - Mapper para conversão DTO/Entity
 */
@Service
@RequiredArgsConstructor
public class PointService {
    // implementação
}
```

#### Padrão de Documentação para Métodos Públicos
```java
/**
 * Busca um ponto específico pelo seu identificador único.
 * 
 * @param id - Identificador único do ponto
 * @return PointResponseDTO com os dados do ponto encontrado
 * @throws EntityNotFoundException quando o ponto não é encontrado
 * 
 * Exemplo de uso:
 * PointResponseDTO point = pointService.findById(1L);
 */
@Transactional(readOnly = true)
public PointResponseDTO findById(Long id) {
    // implementação
}
```

## Estrutura de Pastas
```
.
├── pointtils/                        # Aplicação principal
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pointtils/pointtils/
│   │   │   │   ├── src/                  # Código fonte principal
│   │   │   │   │   ├── application/      # Lógica de aplicação
│   │   │   │   │   │   ├── controllers/  # Controladores REST
│   │   │   │   │   │   ├── dto/          # Objetos de transferência
│   │   │   │   │   │   ├── mapper/       # Mapeadores DTO-Entity
│   │   │   │   │   │   └── services/     # Serviços de negócio
│   │   │   │   │   ├── core/             # Núcleo do domínio
│   │   │   │   │   │   └── domain/       # Entidades e enums
│   │   │   │   │   └── infrastructure/   # Infraestrutura
│   │   │   │   │       ├── configs/      # Configurações
│   │   │   │   │       └── repositories/ # Repositórios
│   │   ├── resources/                # Arquivos de configuração
│   └── test/                         # Testes unitários
├── utils/                            # Utilitários e serviços auxiliares
│   ├── sonarqube/                    # Configuração SonarQube
│   │   └── Dockerfile
│   └── postgres/                     # Configuração PostgreSQL
│       └── Dockerfile
├── docker-compose.yaml               # Orquestração unificada de containers
└── sonarqube-docker-compose.yaml     # Docker-compose antigo (legado)
```

## Como Executar

### Pré-requisitos
- Java 17+
- Maven
- Docker (opcional)

### Comandos Úteis

**Executar localmente:**
```bash
cd pointtils
./mvnw spring-boot:run
```

**Buildar e executar com Docker (nova estrutura unificada):**
```bash
# Executa todos os serviços: aplicação, PostgreSQL e SonarQube
docker-compose up --build

# Ou para executar em background:
docker-compose up -d --build

# Executar apenas serviços específicos:
docker-compose up pointtils pointtils-db  # Apenas app + banco
docker-compose up sonarqube               # Apenas SonarQube
```

**Comandos Docker úteis:**
```bash
# Ver status dos containers
docker-compose ps

# Parar todos os serviços
docker-compose down

# Parar e remover volumes (dados)
docker-compose down -v

# Ver logs de um serviço específico
docker-compose logs pointtils
docker-compose logs sonarqube
```

**Executar testes:**
```bash
./mvnw test
```

**Gerar documentação Swagger:**
Documentação disponível no seguinte endereço:
```
https://backend-v5gs.onrender.com/swagger-ui/index.html
```

## Dicas de Desenvolvimento

### Boas Práticas Gerais
1. **Padrão de Commits**: Siga o Conventional Commits (`feat:`, `fix:`, `docs:`, etc.)
2. **Testes**: Adicione novos testes em `src/test/java` seguindo a mesma estrutura de packages
3. **DTOs**: Sempre use DTOs para comunicação externa, nunca exponha entities diretamente
4. **Documentação**: Mantenha atualizada a documentação Swagger com `@Operation` e `@Tag`
5. **Docker**: Use `docker-compose` para ambiente consistente de desenvolvimento

### Padrões Específicos do Spring Boot
6. **Annotations**: Use annotations apropriadas (`@Service`, `@Repository`, `@Controller`)
7. **Transações**: Sempre anote métodos de escrita com `@Transactional`
8. **Validação**: Implemente validações com Bean Validation quando necessário
9. **Exception Handling**: Use `@ControllerAdvice` para tratamento global de exceções
10. **Configuração**: Mantenha configurações em `application.properties` ou classes `@Configuration`

### Arquitetura e Design
11. **Clean Architecture**: Respeite as dependências direcionais das camadas
12. **SOLID**: Aplique os princípios SOLID, especialmente Single Responsibility
13. **DRY**: Evite duplicação de código, utilize mappers e utilitários
14. **Mappers**: Use classes dedicadas para conversão entre DTOs e Entities
15. **Repository Pattern**: Mantenha repositories simples, lógica complexa fica nos services

## Configurações
As principais configurações estão em:
- `src/main/resources/application.properties` 
- `src/main/java/.../configs/OpenApiConfig.java`

### Variáveis de Ambiente
O projeto utiliza um arquivo `.env.example` como template para configurações sensíveis. Para executar o projeto:

1. Copie o arquivo `.env.example` para `.env`:
```bash
cp .env.example .env
```

2. Edite o `.env` com seus valores reais (não versionado no Git)

Variáveis comuns:
```
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
```

3. Para Docker, certifique-se que as variáveis estão definidas no `docker-compose.yaml` ou no `.env`
