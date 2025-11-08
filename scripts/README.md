# Scripts utilizados por automações do GitHub Actions

Esta pasta contém os scripts que são utilizados pelas automações do GitHub Actions presentes em .github/workflows/.

### Check Deadlines

No arquivo [check_deadlines.py](./check_deadlines.py) está presente um script que busca as tarefas do board que tem como prazo de entrega a data atual e notifica em um servidor do Discord, a fim de facilitar a gestão das tarefas do board do projeto do GitHub realizada pelos colegas AGES IV do projeto.

Para isso, são verificadas todas as issues do projeto, para ambos repositórios (Backend e Frontend), e serão notificadas aquelas que:
- Forem sub-tarefas
- Estiverem abertas
- Tiverem valor de End Date menor ou igual à data atual.

#### Tecnologias utilizadas:

- Python 3.10
- [GitHub GraphQL API](https://docs.github.com/en/graphql)
- [Discord Webhooks](https://discord.com/developers/docs/resources/webhook)