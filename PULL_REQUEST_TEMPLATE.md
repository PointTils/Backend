📍 **Título:** Implementação de endpoints para User Specialties e correção de CORS

📌 **Descrição:**
Esta PR implementa os endpoints completos para gerenciamento de User Specialties, incluindo operações CRUD e associação entre usuários e especialidades. Também inclui uma correção na configuração CORS para permitir o método PATCH.

🛠️ **O que foi feito?**
- [x] Implementação de nova funcionalidade
- [x] Correção de bug
- [ ] Refatoração de código
- [ ] Atualização de documentação

🔍 **Arquivos novos/modificados:**
- `pointtils/src/main/java/com/pointtils/pointtils/src/application/controllers/UserSpecialtyController.java`
- `pointtils/src/main/java/com/pointtils/pointtils/src/application/services/UserSpecialtyService.java`
- `pointtils/src/main/java/com/pointtils/pointtils/src/core/domain/entities/UserSpecialty.java`
- `pointtils/src/main/java/com/pointtils/pointtils/src/infrastructure/repositories/UserSpecialtyRepository.java`
- `pointtils/src/main/java/com/pointtils/pointtils/src/application/dto/AddUserSpecialtiesRequestDTO.java`
- `pointtils/src/main/java/com/pointtils/pointtils/src/application/dto/UserSpecialtiesResponseDTO.java`
- `pointtils/src/main/java/com/pointtils/pointtils/src/application/dto/UserSpecialtyResponseDTO.java`
- `pointtils/src/main/java/com/pointtils/pointtils/src/core/domain/exceptions/UserSpecialtyException.java`
- `pointtils/src/main/java/com/pointtils/pointtils/src/infrastructure/configs/SecurityConfiguration.java` (correção CORS)
- `pointtils/src/test/java/com/pointtils/pointtils/src/application/controllers/UserSpecialtyControllerTest.java`
- `pointtils/src/test/java/com/pointtils/pointtils/src/application/services/UserSpecialtyServiceTest.java`

🧪 **Testes realizados:**
- Testes unitários para UserSpecialtyService
- Testes de integração para UserSpecialtyController
- Testes de validação de CORS para métodos HTTP (GET, POST, PUT, DELETE, PATCH)
- Testes de associação entre usuários e especialidades

👀 **Problemas conhecidos:**
- Nenhum problema identificado
- Todos os endpoints estão funcionando corretamente
- Configuração CORS corrigida para permitir método PATCH

📷 **Anexos**
- [Documentação da API disponível em /swagger-ui.html](http://localhost:8080/swagger-ui.html)
- [Exemplos de uso disponíveis nos testes]

✅ **Checklist**
- [x] Testes foram adicionados/atualizados
- [x] Documentação foi atualizada (via OpenAPI/Swagger)
- [x] O código segue os padrões do projeto
- [x] Build e testes passando localmente

📎 **Referências**
- [Documentação Spring Boot](https://spring.io/projects/spring-boot)
- [Documentação Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Padrões REST API](https://restfulapi.net/)
