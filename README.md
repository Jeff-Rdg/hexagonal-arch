# Hexagonal Architecture — Customer Service

Projeto de referência em **Arquitetura Hexagonal** (Ports & Adapters), com Spring Boot, MongoDB, Kafka e OpenFeign. O domínio central é o gerenciamento de clientes com consulta de endereço por CEP e validação assíncrona de CPF.

---

## Sumário

- [O que é Arquitetura Hexagonal](#o-que-é-arquitetura-hexagonal)
- [Regra de Ouro: Dependências Apontam para Dentro](#regra-de-ouro-dependências-apontam-para-dentro)
- [Estrutura de Pacotes](#estrutura-de-pacotes)
- [Camadas em Detalhe](#camadas-em-detalhe)
  - [Domain (Núcleo)](#1-domain-núcleo)
  - [Ports (Contratos)](#2-ports-contratos)
  - [Use Cases (Aplicação)](#3-use-cases-aplicação)
  - [Adapters In (Primários)](#4-adapters-in-primários)
  - [Adapters Out (Secundários)](#5-adapters-out-secundários)
  - [Config (Injeção de Dependência)](#6-config-injeção-de-dependência)
- [Fluxos de Comunicação](#fluxos-de-comunicação)
- [Validação Arquitetural com ArchUnit](#validação-arquitetural-com-archunit)
- [Infraestrutura (Docker)](#infraestrutura-docker)
- [Tecnologias](#tecnologias)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)

---

## O que é Arquitetura Hexagonal

A Arquitetura Hexagonal, proposta por Alistair Cockburn, organiza o software em três zonas concêntricas:

```
┌─────────────────────────────────────────────────────────────┐
│                         ADAPTERS                            │
│                                                             │
│   ┌───────────────────────────────────────────────────┐    │
│   │                     PORTS                         │    │
│   │                                                   │    │
│   │      ┌─────────────────────────────────────┐     │    │
│   │      │           DOMAIN + USE CASES        │     │    │
│   │      │         (Regras de Negócio)         │     │    │
│   │      └─────────────────────────────────────┘     │    │
│   │                                                   │    │
│   └───────────────────────────────────────────────────┘    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**A ideia central**: o núcleo (domínio + casos de uso) não conhece ninguém de fora. É o mundo externo (banco de dados, APIs, filas, HTTP) que se adapta ao núcleo — nunca o contrário.

---

## Regra de Ouro: Dependências Apontam para Dentro

```
Adapters  →  Ports  →  Use Cases  →  Domain
```

- **Domain** não importa nada do projeto — só Java puro.
- **Use Cases** conhecem apenas Ports (interfaces), nunca implementações concretas.
- **Ports** são interfaces puras — definem contratos, não implementações.
- **Adapters** implementam os Ports e usam frameworks (Spring, MongoDB, Kafka, Feign).
- **Config** é a única camada que conhece tudo — ela faz a montagem (wiring) via injeção de dependência.

Esta regra é verificada automaticamente em tempo de build pelo **ArchUnit** (`LayeredArchitectureTest`).

---

## Estrutura de Pacotes

```
br.com.jrodrigues.hexagonal
│
├── application
│   ├── core
│   │   ├── domain              ← Entidades de negócio (sem framework)
│   │   │   ├── Customer.java
│   │   │   └── Address.java
│   │   └── usecase             ← Casos de uso (orquestram Ports)
│   │       ├── InsertCustomerUseCase.java
│   │       ├── FindCustomerByIdUseCase.java
│   │       ├── UpdateCustomerUseCase.java
│   │       └── DeleteCustomerByIdUseCase.java
│   └── ports
│       ├── in                  ← O que o mundo externo pode pedir ao sistema
│       │   ├── InsertCustomerInputPort.java
│       │   ├── FindCustomerByIdInputPort.java
│       │   ├── UpdateCustomerInputPort.java
│       │   └── DeleteCustomerByIdInputPort.java
│       └── out                 ← O que o sistema precisa do mundo externo
│           ├── InsertCustomerOutputPort.java
│           ├── FindByCustomerByIdOutputPort.java
│           ├── UpdateCustomerOutputPort.java
│           ├── DeleteCustomerByIdOutputPort.java
│           ├── FindAddressByZipCodeOutputPort.java
│           └── SendCpfForValidationOutputPort.java
│
├── adapters
│   ├── in                      ← Adaptadores primários (acionam Use Cases)
│   │   ├── controller          ← REST API
│   │   │   ├── CustomerController.java
│   │   │   ├── request/CustomerRequest.java
│   │   │   ├── response/CustomerResponse.java
│   │   │   └── mapper/CustomerMapper.java
│   │   └── consumer            ← Kafka Consumer
│   │       ├── ReceiveValidatedCpfConsumer.java
│   │       ├── message/CustomerMessage.java
│   │       └── mapper/CostumerMessageMapper.java
│   └── out                     ← Adaptadores secundários (implementam OutputPorts)
│       ├── repository          ← MongoDB
│       │   ├── InsertCustomerAdapter.java
│       │   ├── FindCustomerByIdAdapter.java
│       │   ├── UpdateCustomerAdapter.java
│       │   ├── DeleteCustomerByIdAdapter.java
│       │   ├── CustomerRepository.java
│       │   ├── entity/CustomerEntity.java
│       │   ├── entity/AddressEntity.java
│       │   └── mapper/CustomerEntityMapper.java
│       ├── client              ← OpenFeign (serviço de CEP externo)
│       │   ├── FindAddressByZipCodeAdapter.java
│       │   ├── FindAddressByZipCodeClient.java
│       │   ├── response/AddressResponse.java
│       │   └── mapper/AddressResponseMapper.java
│       └── producer            ← Kafka Producer (validação de CPF)
│           └── SendCpfValidationAdapter.java
│
└── config                      ← Wiring: monta os Use Cases com seus Adapters
    ├── InsertCustomerConfig.java
    ├── FindCustomerByIdConfig.java
    ├── UpdateCustomerConfig.java
    ├── DeleteCustomerConfig.java
    ├── KafkaProducerConfig.java
    └── KafkaConsumerConfig.java
```

---

## Camadas em Detalhe

### 1. Domain (Núcleo)

O núcleo puro do negócio. Sem anotações de framework. Sem dependências externas.

**`Customer`** — entidade principal:
- `id`, `name`, `cpf`, `isValidCpf`, `address`

**`Address`** — value object embutido:
- `street`, `city`, `state`, `number`

Essas classes existem apenas para representar o modelo de negócio. Elas nunca sabem se vão parar num banco SQL, MongoDB ou em memória.

---

### 2. Ports (Contratos)

Interfaces que definem os contratos de comunicação entre camadas. São a fronteira do hexágono.

#### Input Ports — *"O que o sistema expõe"*

| Interface | Método |
|-----------|--------|
| `InsertCustomerInputPort` | `execute(Customer, String zipCode, Long houseNumber)` |
| `FindCustomerByIdInputPort` | `execute(String id) → Customer` |
| `UpdateCustomerInputPort` | `execute(Customer, String zipCode, Long addressNumber)` |
| `DeleteCustomerByIdInputPort` | `execute(String id)` |

#### Output Ports — *"O que o sistema precisa"*

| Interface | Responsabilidade |
|-----------|-----------------|
| `InsertCustomerOutputPort` | Persistir cliente |
| `FindByCustomerByIdOutputPort` | Buscar cliente por ID |
| `UpdateCustomerOutputPort` | Atualizar cliente |
| `DeleteCustomerByIdOutputPort` | Deletar cliente |
| `FindAddressByZipCodeOutputPort` | Buscar endereço por CEP |
| `SendCpfForValidationOutputPort` | Enviar CPF para validação |

> Os Use Cases dependem **apenas dessas interfaces**. Isso garante que qualquer tecnologia pode ser trocada sem tocar na lógica de negócio.

---

### 3. Use Cases (Aplicação)

Orquestram o fluxo de negócio combinando Output Ports. Cada Use Case implementa um Input Port.

#### `InsertCustomerUseCase`
```
1. Busca endereço pelo CEP           → FindAddressByZipCodeOutputPort
2. Define o número da residência     → (lógica de domínio)
3. Persiste o cliente                → InsertCustomerOutputPort
4. Envia CPF para validação assínc.  → SendCpfForValidationOutputPort
```

#### `FindCustomerByIdUseCase`
```
1. Busca cliente por ID              → FindByCustomerByIdOutputPort
2. Lança exceção se não encontrado   → (lógica de domínio)
```

#### `UpdateCustomerUseCase`
```
1. Verifica existência do cliente    → FindCustomerByIdInputPort
2. Busca novo endereço pelo CEP      → FindAddressByZipCodeOutputPort
3. Atualiza o cliente                → UpdateCustomerOutputPort
```

#### `DeleteCustomerByIdUseCase`
```
1. Verifica existência do cliente    → FindCustomerByIdInputPort
2. Deleta o cliente                  → DeleteCustomerByIdOutputPort
```

---

### 4. Adapters In (Primários)

Adaptadores que **acionam** os Use Cases. São as entradas do sistema.

#### REST Controller (`/api/v1/customers`)

| Método | Endpoint | Ação |
|--------|----------|------|
| `POST` | `/api/v1/customers` | Criar cliente |
| `GET` | `/api/v1/customers/{id}` | Buscar cliente |
| `PUT` | `/api/v1/customers/{id}` | Atualizar cliente |
| `DELETE` | `/api/v1/customers/{id}` | Deletar cliente |

O controller recebe um `CustomerRequest` (DTO), usa **MapStruct** para converter em `Customer` (domínio) e chama o Input Port correspondente. A resposta volta como `CustomerResponse` (DTO).

```
HTTP Request
    ↓
CustomerController
    ↓  (MapStruct: CustomerRequest → Customer)
InputPort.execute(customer, ...)
    ↓
Use Case
```

#### Kafka Consumer (`cpf-validated`)

Ouve o tópico `cpf-validated`. Quando uma mensagem chega com o resultado da validação de CPF, converte para `Customer` via MapStruct e chama `UpdateCustomerInputPort` para persistir o novo status.

```
Kafka topic: cpf-validated
    ↓
ReceiveValidatedCpfConsumer
    ↓  (MapStruct: CustomerMessage → Customer)
UpdateCustomerInputPort.execute(customer, ...)
    ↓
UpdateCustomerUseCase
```

---

### 5. Adapters Out (Secundários)

Adaptadores que **implementam** os Output Ports. São as saídas do sistema.

#### Repository Adapters (MongoDB)

Cada operação de banco tem seu próprio adapter. Todos delegam ao `CustomerRepository` (Spring Data) e usam **MapStruct** para converter entre `Customer` (domínio) e `CustomerEntity` (MongoDB).

```
InsertCustomerOutputPort  →  InsertCustomerAdapter  →  CustomerRepository (MongoDB)
FindByCustomerByIdOutputPort  →  FindCustomerByIdAdapter  →  CustomerRepository
UpdateCustomerOutputPort  →  UpdateCustomerAdapter  →  CustomerRepository
DeleteCustomerByIdOutputPort  →  DeleteCustomerByIdAdapter  →  CustomerRepository
```

A coleção MongoDB é `customers`. `AddressEntity` é um documento embutido em `CustomerEntity`.

#### OpenFeign Client (Serviço de CEP)

```
FindAddressByZipCodeOutputPort  →  FindAddressByZipCodeAdapter
                                        ↓
                               FindAddressByZipCodeClient (Feign)
                                        ↓
                          GET http://localhost:8082/addresses/{zipCode}
                                  (WireMock em dev)
```

O `AddressResponseMapper` converte o `AddressResponse` (DTO do cliente Feign) para `Address` (domínio).

#### Kafka Producer (Validação de CPF)

```
SendCpfForValidationOutputPort  →  SendCpfValidationAdapter
                                         ↓
                                  KafkaTemplate<String, String>
                                         ↓
                               topic: cpf-validation
```

---

### 6. Config (Injeção de Dependência)

A camada `config` é a única que **conhece todas as outras**. Ela é responsável por montar cada Use Case com seus respectivos Adapters, usando Spring `@Bean`.

```java
// Exemplo conceitual
@Bean
InsertCustomerInputPort insertCustomerUseCase(
    FindAddressByZipCodeOutputPort findAddress,
    InsertCustomerOutputPort insertCustomer,
    SendCpfForValidationOutputPort sendCpf
) {
    return new InsertCustomerUseCase(findAddress, insertCustomer, sendCpf);
}
```

Isso garante que os Use Cases recebem interfaces (não implementações concretas) — respeitando a inversão de dependência.

---

## Fluxos de Comunicação

### Criação de Cliente (síncrono + assíncrono)

```
┌─────────────────────────────────────────────────────────────────────────┐
│  POST /api/v1/customers                                                 │
│      │                                                                  │
│      ▼                                                                  │
│  CustomerController ──(MapStruct)──► InsertCustomerInputPort            │
│                                              │                          │
│                                              ▼                          │
│                                    InsertCustomerUseCase                │
│                                     │         │         │               │
│                            ┌────────┘  ┌──────┘  ┌─────┘               │
│                            ▼           ▼          ▼                     │
│                    FindAddress    InsertCustomer  SendCpf               │
│                    ByZipCode      OutputPort      ForValidation         │
│                    OutputPort         │           OutputPort            │
│                        │             │               │                  │
│                        ▼             ▼               ▼                  │
│                    Feign Client  MongoDB         KafkaTemplate          │
│                    (CEP service) (save)          (cpf-validation)       │
└─────────────────────────────────────────────────────────────────────────┘

                              ┌──────────────────────────────────────────┐
                              │  (assíncrono — serviço externo)          │
                              │  cpf-validation topic                    │
                              │      │                                   │
                              │      ▼                                   │
                              │  [Serviço de Validação de CPF]           │
                              │      │                                   │
                              │      ▼                                   │
                              │  cpf-validated topic                     │
                              │      │                                   │
                              │      ▼                                   │
                              │  ReceiveValidatedCpfConsumer             │
                              │      │                                   │
                              │      ▼                                   │
                              │  UpdateCustomerInputPort                 │
                              │      │                                   │
                              │      ▼                                   │
                              │  MongoDB (isValidCpf atualizado)         │
                              └──────────────────────────────────────────┘
```

### Busca de Cliente

```
GET /api/v1/customers/{id}
    ↓
CustomerController
    ↓
FindCustomerByIdInputPort.execute(id)
    ↓
FindCustomerByIdUseCase
    ↓
FindByCustomerByIdOutputPort.find(id)
    ↓
MongoDB → CustomerEntity
    ↓ (MapStruct)
Customer (domínio)
    ↓ (MapStruct)
CustomerResponse → HTTP 200
```

---

## Validação Arquitetural com ArchUnit

O arquivo [LayeredArchitectureTest.java](src/test/java/br/com/jrodrigues/hexagonal/architecture/LayeredArchitectureTest.java) usa **ArchUnit** para garantir que as regras de dependência entre camadas não sejam violadas.

As regras verificadas são:

| Camada | Pode ser acessada por |
|--------|-----------------------|
| `adapters.in` | apenas `config` |
| `adapters.out` | apenas `config` |
| `usecase` | apenas `config` |
| `ports.in` | `usecase` e `adapters.in` |
| `ports.out` | `usecase` e `adapters.out` |
| `config` | ninguém (é isolada) |

Se um desenvolvedor tentar importar um adapter diretamente dentro de um use case, o build quebra. A arquitetura é auto-defensiva.

---

## Infraestrutura (Docker)

O arquivo `docker-local/docker-compose.yml` sobe todo o ambiente de desenvolvimento local:

| Serviço | Porta | Finalidade |
|---------|-------|-----------|
| **WireMock** | 8082 | Mock do serviço externo de CEP |
| **Zookeeper** | 2181 | Coordenação do Kafka |
| **Kafka** | 9092 | Message broker |
| **Kafdrop** | 9000 | Interface web do Kafka |
| **MongoDB** | 27017 | Banco de dados principal |
| **Mongo Express** | 8083 | Interface web do MongoDB |

Os mocks do WireMock ficam em `docker-local/mappings/`.

---

## Tecnologias

| Tecnologia | Versão | Uso |
|-----------|--------|-----|
| Java | 17 | Linguagem |
| Spring Boot | 4.1.0 | Framework principal |
| Spring Data MongoDB | — | Persistência |
| Spring Kafka | — | Mensageria |
| OpenFeign | — | Cliente HTTP declarativo |
| MapStruct | 1.5.5 | Mapeamento entre camadas |
| Lombok | — | Redução de boilerplate |
| ArchUnit | — | Validação de arquitetura |
| MongoDB | — | Banco de dados |
| Apache Kafka | — | Fila de mensagens |
| WireMock | — | Mock de serviços externos |
| Docker Compose | — | Infraestrutura local |

---

## Como Executar

**Pré-requisitos**: Docker, Java 17, Maven

**1. Suba a infraestrutura local:**
```bash
docker-compose -f docker-local/docker-compose.yml up -d
```

**2. Execute a aplicação:**
```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8081`.

---

## Endpoints da API

### Criar Cliente
```http
POST /api/v1/customers
Content-Type: application/json

{
  "name": "João Silva",
  "cpf": "123.456.789-09",
  "zipCode": "01310-100",
  "addressNumber": 1500
}
```

### Buscar Cliente
```http
GET /api/v1/customers/{id}
```

### Atualizar Cliente
```http
PUT /api/v1/customers/{id}
Content-Type: application/json

{
  "name": "João Silva",
  "cpf": "123.456.789-09",
  "zipCode": "04538-132",
  "addressNumber": 200
}
```

### Deletar Cliente
```http
DELETE /api/v1/customers/{id}
```

---

## Princípio Fundamental

> **Adapters se adaptam ao núcleo. O núcleo nunca se adapta aos adapters.**

Trocar MongoDB por PostgreSQL? Implemente novos `*Adapter` de repository, sem tocar em nenhum Use Case.  
Trocar Kafka por RabbitMQ? Implemente `SendCpfValidationAdapter` para RabbitMQ, sem tocar no Use Case.  
Trocar REST por gRPC? Implemente um novo Adapter In para gRPC, sem tocar em nada mais.

Essa é a promessa — e a força — da Arquitetura Hexagonal.
