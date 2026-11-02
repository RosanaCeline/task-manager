# TaskManager -- Gerenciador de Tarefas

Aplicação desenvolvida em Java como parte da **Unidade 5 -- Capítulo
1**, do curso Capacita iRede. O projeto evolui uma versão de console
para uma aplicação gráfica com JavaFX, persistência em MySQL e testes
automatizados.

## Funcionalidades

-   Cadastro, edição, listagem e remoção de tarefas.
-   Criação de subtarefas vinculadas a tarefas principais.
-   Marcação de tarefas como concluídas.
-   Persistência dos dados em banco de dados relacional.

## Tecnologias e conceitos

-   **Java 21** e **Maven**.
-   **JavaFX**, FXML, Scene Builder e CSS para a interface gráfica.
-   **JDBC** e **MySQL** para acesso e persistência dos dados.
-   **Generics** para reutilização de código, por meio da interface
    `Repository<T>`.
-   **JUnit 5** para testes unitários e de integração.
-   Programação Orientada a Objetos: encapsulamento, herança e
    polimorfismo.

## Organização

O código está dividido em pacotes com responsabilidades específicas:

-   `app`: inicialização da aplicação.
-   `controller`: controle das telas e interação com o usuário.
-   `dao`: conexão com o banco de dados e fábrica de conexões.
-   `model`: entidades `Task` e `TaskPrioritaria`.
-   `repository`: contrato genérico e operações de persistência.
-   `exceptions`: exceções personalizadas.
-   `resources`: arquivos FXML e CSS.
-   `src/test`: testes automatizados.

## Banco de dados

O projeto utiliza MySQL. A tabela `tarefas` armazena identificador,
título, descrição, situação de conclusão e `tarefa_pai_id`, que
relaciona subtarefas às tarefas principais. A chave estrangeira utiliza
`ON DELETE CASCADE`.

Há dois scripts SQL:

-   `schema.sql`: estrutura do banco utilizado pela aplicação.
-   `schema-test.sql`: estrutura do banco separado utilizado nos testes.

As classes `Conexao` e `ConexaoTeste` fornecem acesso aos respectivos
bancos. Configure as credenciais e permissões do MySQL antes de executar
o projeto.

## Como executar

**Pré-requisitos:** JDK 21, Maven e MySQL Server.

1.  Clone o repositório:

    ``` bash
    git clone https://github.com/RosanaCeline/task-manager
    ```

2.  Execute `schema.sql` e `schema-test.sql` no MySQL.

3.  Configure as credenciais nas classes `Conexao` e `ConexaoTeste`.

4.  Abra o projeto na IDE e execute `MainApplication`.

Para executar os testes:

``` bash
mvn clean test
```

Os relatórios são gerados em `target/surefire-reports/`.

## Testes

Os testes unitários verificam a criação, validação e comportamento das
tarefas. Os testes de integração verificam as operações do
`TaskRepository` com o banco de testes, incluindo inserção, consulta,
conclusão, remoção, relacionamento entre tarefas e exclusão em cascata.

## Autor

**Rosana Celine Pinheiro Damaceno**\
Projeto desenvolvido para o curso Capacita iRede.
