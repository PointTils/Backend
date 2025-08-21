# Configuração do SonarCloud e SonarQube Local para PointTils

Este documento descreve o processo de configuração do SonarCloud (nuvem) e SonarQube (local) para o projeto PointTils.

## Pré-requisitos

1. Conta no [SonarCloud](https://sonarcloud.io/)
2. Projeto criado no SonarCloud com organização `pointtils`
3. Token de acesso do SonarCloud

## Configuração no GitHub

### 1. Adicionar Secrets no GitHub

No repositório do GitHub, vá para:
**Settings → Secrets and variables → Actions**

Adicione as seguintes secrets:

- `SONAR_TOKEN`: Token de acesso do SonarCloud
- `SONAR_HOST_URL`: `https://sonarcloud.io` (já configurado)

### 2. Configurar o Workflow

O arquivo `.github/workflows/sonarcloud.yaml` já está configurado com:

- Trigger em push/pull request para branches main e development
- Setup do JDK 17
- Execução de testes com cobertura
- Análise SonarCloud

### 3. Execução do Workflow

O workflow será executado automaticamente em:
- Push para as branches `main` e `development`
- Pull requests para essas branches

## Configuração do Maven

O `pom.xml` contém:

### Propriedades do SonarCloud
```xml
<sonar.organization>pointtils</sonar.organization>
<sonar.host.url>https://sonarcloud.io</sonar.host.url>
<sonar.coverage.jacoco.xmlReportPaths>${project.build.directory}/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
<sonar.junit.reportPaths>${project.build.directory}/surefire-reports</sonar.junit.reportPaths>
```

### Perfil de Cobertura
O perfil `coverage` configura o JaCoCo para:
- Preparar agente de cobertura
- Gerar relatório
- Verificar thresholds mínimos

## Thresholds de Cobertura

Os thresholds mínimos configurados são:

- **Linhas**: 80%
- **Branches**: 70% 
- **Instruções**: 80%
- **Métodos**: 80%
- **Classes**: 90%

Se a cobertura ficar abaixo desses valores, o build falhará.

## Execução Local

Para executar testes com cobertura localmente:

```bash
cd pointtils
mvn clean test -Pcoverage
```

O relatório de cobertura será gerado em:
`target/site/jacoco/index.html`

## Troubleshooting

### Problemas Comuns

1. **Token inválido**: Verifique se o `SONAR_TOKEN` está correto
2. **Organização não encontrada**: Confirme o nome da organização no SonarCloud
3. **Cobertura insuficiente**: Execute todos os testes e verifique se há código não testado

### Logs de Debug

Para debug, adicione `-X` ao comando Maven:

```bash
mvn clean test -Pcoverage -X
```

## Integração Contínua

O SonarCloud se integra com:

- **GitHub Checks**: Status de qualidade do código em PRs
- **Quality Gate**: Aprovação/reprovação automática baseada em métricas
- **Dashboard**: Visualização de métricas de qualidade do código

## Configuração do SonarQube Local

### 1. Subir SonarQube localmente com Docker

```bash
# Usando docker-compose
docker-compose -f sonarqube-docker-compose.yaml up -d

# Ou usando docker diretamente
docker run -d --name sonarqube \
  -p 9000:9000 \
  sonarqube:9.9-community
```

### 2. Acessar o SonarQube
- URL: http://localhost:9000
- Login padrão: admin / admin
- **Importante**: Alterar a senha na primeira vez

### 3. Criar projeto no SonarQube
1. Acessar http://localhost:9000
2. Criar novo projeto manualmente
3. Gerar token para o projeto
4. Escolher "Maven" como método de análise

### 4. Executar análise local

```bash
cd pointtils

# Com propriedades definidas no pom.xml
mvn clean verify sonar:sonar

# Ou definindo propriedades via linha de comando
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=SEU_TOKEN_GERADO
```

### 5. Arquivo de configuração

O arquivo `sonar-project.properties` já está configurado com:
- Chave e nome do projeto
- Diretórios de código fonte e testes
- Caminhos dos relatórios de cobertura
- Configurações específicas do Java 17

## Limitações das Versões Gratuitas

### SonarCloud Community
- ✅ Análise de projetos públicos gratuita
- ✅ Integração com GitHub
- ❌ Análise de Pull/Merge Requests (apenas planos pagos)
- ❌ Portfólios de projetos

### SonarQube Community Edition
- ✅ Execução local sem custo
- ✅ Análise completa da branch principal
- ❌ Análise de múltiplas branches
- ❌ Recursos empresariais avançados

## Próximos Passos

1. Configurar Quality Gate no SonarCloud/SonarQube
2. Adicionar badges de cobertura no README
3. Configurar notificações de qualidade do código
4. Implementar mais testes para aumentar a cobertura
5. Configurar análise automática em PRs (se usando versão paga)
