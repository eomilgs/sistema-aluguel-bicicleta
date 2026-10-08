# Sistema de Aluguel de Bicicletas

Projeto acadêmico de Banco de Dados com **Java 21**, **Maven**, **Hibernate ORM direto** e **SQLite**. Não utiliza Spring Boot, Spring Data, API ou interface gráfica: o sistema funciona inteiramente pelo terminal.

## Modelo de dados

```text
Cliente (1) -------- (N) Aluguel (N) -------- (1) Bicicleta
```

- Um cliente pode realizar vários aluguéis.
- Uma bicicleta pode aparecer em vários aluguéis ao longo do tempo.
- Cada aluguel referencia exatamente um cliente e uma bicicleta.

As relações estão anotadas com `@OneToMany` em `Cliente` e `Bicicleta`, e com `@ManyToOne`/`@JoinColumn` em `Aluguel`.

## Estrutura

```text
src/main/java/br/edu/exemplo/aluguel/
  config/HibernateUtil.java       SessionFactory do Hibernate
  model/                          entidades Cliente, Bicicleta e Aluguel
  dao/                            CRUD direto com Session e Transaction
  service/AluguelService.java     regras de realizar/finalizar aluguel
  Main.java                       menu interativo do terminal
src/main/resources/
  hibernate.cfg.xml               conexão e criação automática das tabelas
pom.xml                           dependências Maven e Java 21
```

## Banco e configuração

O arquivo `hibernate.cfg.xml` usa `jdbc:sqlite:banco-aluguel.db`. Assim, o SQLite cria e mantém o banco no arquivo `banco-aluguel.db`, na raiz do projeto. A propriedade `hibernate.hbm2ddl.auto=update` cria ou atualiza as tabelas automaticamente a partir das entidades.

## Como executar

1. Instale JDK 21 e Maven.
2. Abra esta pasta como projeto Maven no IntelliJ IDEA/Eclipse, ou abra um terminal nela.
3. Execute:

   ```bash
   mvn clean compile exec:java
   ```

## Como testar pelo menu

1. Escolha `1` e cadastre um cliente.
2. Escolha `5` e cadastre uma bicicleta, informando uma diária positiva.
3. Escolha `9`, informe os IDs criados e datas no formato `dd/MM/aaaa`. A bicicleta passa para **Alugada** e o total é calculado pelos dias entre as datas multiplicados pela diária.
4. Escolha `10` para consultar o aluguel e seus relacionamentos.
5. Escolha `11` para registrar a devolução. A bicicleta volta a ficar **Disponível**.
6. Use `2`, `3`, `4`, `6`, `7` e `8` para testar os demais CRUDs. Clientes e bicicletas que tenham aluguéis registrados não podem ser excluídos, preservando o histórico e as chaves estrangeiras.

## Onde está o CRUD

- Cliente: `ClienteDao` e opções 1 a 4 do `Main`.
- Bicicleta: `BicicletaDao` e opções 5 a 8 do `Main`.
- Aluguel: `AluguelDao`, `AluguelService` e opções 9 a 11 do `Main`.

Os DAOs usam `Session`, `Transaction`, `persist`, `get`, `merge`, `remove` e consultas HQL diretamente, demonstrando Hibernate sem Spring Data.
