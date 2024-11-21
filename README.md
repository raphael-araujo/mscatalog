# Product Management

[//]: # (::clipboard:: Catálogo de Produtos)

[//]: # (Está é uma API que possibilita a Criação de um produto, Atualização de um produto, Busca de um produto por Código, Lista de produtos, Lista de produtos filtrados por nome, descrição e preço e Deleção de um produto.)


[//]: # (:computer: A aplicação está sendo executada na porta 9999, rodando no perfil de 'test' com o banco de dados H2 e contém um seeding de alguns produtos que são inseridos ao iniciar a aplicação.)



A API consiste em um microsserviço desenvolvido em Spring Boot para gerenciamento de produtos.

<br>

-------------------------------------------------------------------------------------------------------

### Tecnologias Utilizadas

|    Java    |  Spring  |
|:----------:|:--------:|
|    17.*    |  3.3.5   |

<br>

-------------------------------------------------------------------------------------------------------

### Endpoints:

| Método HTTP |                  URL                  |             Descrição              |      Query params       |
|:-----------:|:-------------------------------------:|:----------------------------------:|:-----------------------:|
|    POST     |    http://localhost:9999/products     |          Cria um produto           |                         |
|     GET     |  http://localhost:9999/products/{id}  |      Busca um produto por ID       |                         |
|     GET     |    http://localhost:9999/products     |      Lista todos os produtos       |                         |
|     PUT     |  http://localhost:9999/products/{id}  |        Atualiza um produto         |                         |
|   DELETE    |  http://localhost:9999/products/{id}  |         Exclui um produto          |                         |
|     GET     | http://localhost:9999/products/search | Busca e lista produtos por filtros | q, min_price, max_price |


<br>

-------------------------------------------------------------------------------------------------------

## Payload

Payload esperado na criação ou atualização de um produto:

  ```json
    {
      "name": "string",
      "description": "string",
      "price": 1.0
    } 
  ```

Resposta:

  ```json
    {
      "id": 1,
      "name": "string",
      "description": "string",
      "price": 1.0
    } 
  ```

<br>

-------------------------------------------------------------------------------------------------------

## Iniciando a aplicação:

```
 git clone https://github.com/raphael-araujo/mscatalog.git
```

```
mvn spring-boot:run
```
