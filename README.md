# Desafio Técnico: Microsserviço de Gerenciamento de Clientes e Score

Este repositório contém a solução desenvolvida para o desafio técnico de Engenheiro de Software Pleno. O projeto consiste em um ecossistema distribuído focado na escalabilidade, desacoplamento e resiliência, composto por dois microsserviços integrados de forma síncrona e assíncrona.

## 🚀 Arquitetura e Diferenciais Técnicos

Para ir além do CRUD convencional pedido pela especificação, adotei padrões arquiteturais modernos de sistemas distribuídos:

1. [cite_start]**Customer-Service (`CustomerManager`)**: Responsável por gerenciar os ciclos de vida dos clientes (CRUD) [cite: 3, 4][cite_start], garantindo regras rígidas de validação de dados (Validador customizado de CPF em anotações Bean Validation) [cite: 53] [cite_start]e segurança de endpoints[cite: 59].
2. **Score-Service (`ScoreService`)**: Atua como o sistema provedor de score. [cite_start]A integração ocorre via **HTTP (RestTemplate/WebClient)** para consultas sob demanda[cite: 5, 29, 30], mas inclui resiliência assíncrona.
3. **Mensageria com RabbitMQ**: Quando um novo cliente é cadastrado no `CustomerManager`, um evento assíncrono `customer.created` é publicado. O `ScoreService` escuta essa fila e processa a geração assíncrona/prévia do Score do cliente em segundo plano, diminuindo o tempo de resposta em chamadas futuras.
4. **Camada de Cache com Redis**: Consultas de Score recorrentes são cacheadas no Redis. Isso previne o efeito de gargalo (*bottleneck*), protegendo a base de dados relacional e garantindo latência abaixo de milissegundos.
5. **Conteinerização Completa**: Toda a infraestrutura (aplicações, RabbitMQ, Redis, Banco de Dados H2) está orquestrada via Docker Compose, permitindo execução idêntica em qualquer ambiente de desenvolvimento ou CI/CD.

---

## 🛠️ Requisitos para Execução

Antes de iniciar, certifique-se de ter instalado em sua máquina:
- **Docker** e **Docker Compose**
- **Make** (Utilitário para facilitação de comandos de terminal)
- **Java 17** e **Maven** (Caso queira rodar os testes localmente fora do container)

---

## 🏃 Como Iniciar a Aplicação

O projeto conta com um arquivo `Makefile` na raiz que simplifica toda a orquestração do ambiente.

Para subir todo o ecossistema (Customer Service, Score Service, RabbitMQ, Redis e Banco de Dados), basta rodar o comando:

```bash
make up

```

*Caso seu ambiente não possua o comando `make`, execute manualmente:* `docker-compose up -d --build`

Para encerrar a execução e limpar os containers da máquina:

```bash
make down

```

---

## 🧪 Como Executar os Testes

A suíte de testes automatizados foi dividida estrategicamente entre testes unitários (focados em regras de negócio rápidas) e testes de integração (interagindo com persistência real em H2).

### Via Terminal (Makefile / Maven)

Para rodar todos os testes do projeto:

```bash
make test

```

---

## 🔒 Segurança (Spring Security)

Todos os endpoints estão protegidos por **Basic Authentication**. Conforme os requisitos, foram criados dois perfis:

* 
**USER**: Permissão apenas para operações de consulta (`GET`).


* *Credenciais padrão:* Usuário: `user` | Senha: `user123`


* 
**ADMIN**: Permissão total para operações de escrita (`POST`, `PUT`, `DELETE`) e consultas.


* *Credenciais padrão:* Usuário: `admin` | Senha: `admin123`



---

## 📌 Endpoints Disponíveis e Exemplos de Utilização

### 1. Gerenciamento de Clientes (`CustomerManager` - Porta `8080`)

#### **Cadastrar Cliente**

* **HTTP Method**: `POST`
* 
**Endpoint**: `/customers` 


* 
**Permissão**: `ADMIN` 


* **Payload Exemplo (Request)**:

```json
{
  "name": "João da Silva",
  "cpf": "12345678901",
  "email": "joao@email.com",
  "status": "ACTIVE"
}

```

* **Resposta Esperada (`201 Created`)**:

```json
{
  "id": 1,
  "name": "João da Silva",
  "cpf": "12345678901",
  "email": "joao@email.com",
  "status": "ACTIVE"
}

```

#### **Buscar Cliente por ID**

* **HTTP Method**: `GET`
* 
**Endpoint**: `/customers/1` 


* 
**Permissão**: `USER` ou `ADMIN` 


* **Resposta Esperada (`200 OK`)**: Retorna o JSON do cliente.

#### **Buscar Score do Cliente**

Busca o CPF do cliente localmente e aciona de forma síncrona/via cache o microsserviço de Score.

* **HTTP Method**: `GET`
* 
**Endpoint**: `/customers/1/score` 


* 
**Permissão**: `USER` ou `ADMIN` 


* **Resposta Esperada (`200 OK`)**:

```json
{
  "cpf": "12345678901",
  "score": 750,
  "classification": "LOW_RISK"
}

```

#### **Outras Rotas de Clientes**:

* 
`PUT /customers/{id}`: Atualiza os dados do cliente (Apenas `ADMIN`).


* 
`DELETE /customers/{id}`: Remove o cliente da base (Apenas `ADMIN`).


* 
`GET /customers?status=ACTIVE`: Lista clientes filtrados por status.


* 
`GET /customers/search?name=joao`: Busca clientes por nome usando query nativa do banco.



---

### 2. Provedor de Score (`ScoreService` - Porta `8081`)

Este serviço funciona de forma encapsulada no ecossistema, mas expõe seu endpoint para consumo direto se necessário.

#### **Consultar Score por CPF**

* **HTTP Method**: `GET`
* 
**Endpoint**: `/score/{cpf}` 


* **Resposta Esperada (`200 OK`)**:

```json
{
  "cpf": "12345678901",
  "score": 820,
  "classification": "LOW_RISK"
}

```

---

## 🛡️ Tratamento de Erros e Resiliência

A aplicação conta com um `GlobalExceptionHandler` que captura falhas e padroniza respostas amigáveis para o cliente da API:

* 
**CPF Duplicado / Email em Uso**: Retorna `400 Bad Request` com mensagem de negócio explícita.


* 
**Cliente Inexistente**: Retorna `404 Not Found`.


* 
**Falha/Timeout no Provedor de Score Externo**: O sistema captura erros HTTP através de blocos `try-catch/CircuitBreaker`, respondendo com status apropriado sem derrubar ou quebrar o fluxo principal da aplicação de clientes.

