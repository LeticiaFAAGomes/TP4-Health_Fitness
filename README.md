# 🏋️ Sistema de Gestão de Alunos

### Teste de Performance 4

#### Refatoração para Arquitetura Orientada a Eventos com Spring Boot + RabbitMQ

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Eureka](https://img.shields.io/badge/Eureka-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

Aplicação distribuída para **gestão de alunos e seus históricos**, desenvolvida utilizando uma arquitetura baseada em **microsserviços** com **Spring Boot** e **Spring Cloud**.

Nesta quarta entrega, o sistema foi refatorado para utilizar uma **Arquitetura Orientada a Eventos (Event-Driven Architecture)**, substituindo a comunicação síncrona entre `ms-gestao-alunos` e `ms-historico` por uma comunicação **assíncrona utilizando RabbitMQ**.

O projeto foi desenvolvido como quarta entrega do TP da disciplina de **Desenvolvimento de Softwares Escaláveis**, com foco em comunicação assíncrona, mensageria, padrões de mensagens e desacoplamento entre microsserviços.

---

## 📌 Objetivos

- Refatorar a comunicação entre os microsserviços para um modelo orientado a eventos.
- Utilizar o RabbitMQ como message broker.
- Implementar comunicação assíncrona entre `ms-gestao-alunos` e `ms-historico`.
- Aplicar padrões de mensagens utilizando comandos e resultados.
- Utilizar abstrações do Spring Boot para integração com RabbitMQ.
- Reduzir o acoplamento entre os microsserviços.
- Melhorar a resiliência e a escalabilidade da comunicação.
- Demonstrar o funcionamento do fluxo de mensagens de forma prática.

---

## ✅ Funcionalidades

- Cadastro de alunos
- Listagem de alunos
- Busca de aluno por ID
- Atualização de alunos
- Remoção de alunos
- Registro automático de histórico
- Consulta do histórico de um aluno
- Comunicação assíncrona entre microsserviços
- Publicação de mensagens utilizando RabbitMQ
- Consumo de mensagens utilizando RabbitMQ
- Confirmação do processamento do histórico
- Tratamento de resultado confirmado ou recusado
- Descoberta de serviços utilizando Eureka
- Roteamento das requisições através do API Gateway

---

## 🏛 Arquitetura

A aplicação utiliza uma arquitetura baseada em **microsserviços**, com comunicação assíncrona entre os serviços de gestão de alunos e histórico.

```text
                              ┌───────────────────────┐
                              │        CLIENTE        │
                              │      Swagger /       │
                              │       Front-end      │
                              └───────────┬───────────┘
                                          │
                                          │ HTTP / REST
                                          ▼
                              ┌───────────────────────┐
                              │      API GATEWAY      │
                              │         :8080         │
                              └───────────┬───────────┘
                                          │
                                          ▼
                              ┌───────────────────────┐
                              │   MS-GESTAO-ALUNOS    │
                              │         :8081         │
                              └───────────┬───────────┘
                                          │
                                          │ RegistrarHistoricoCommand
                                          ▼
                              ┌───────────────────────┐
                              │       RABBITMQ        │
                              │         :5672         │
                              │                       │
                              │   alunos.exchange     │
                              └───────────┬───────────┘
                                          │
                              historico.registrar
                                          │
                                          ▼
                              ┌───────────────────────┐
                              │     MS-HISTORICO      │
                              │         :8082         │
                              └───────────┬───────────┘
                                          │
                                          │ Salva histórico
                                          ▼
                              ┌───────────────────────┐
                              │       BANCO H2        │
                              └───────────────────────┘
                                          │
                                          │ ResultadoHistorico
                                          ▼
                              ┌───────────────────────┐
                              │       RABBITMQ        │
                              │                       │
                              │ historico.confirmado  │
                              │ historico.recusado    │
                              └───────────┬───────────┘
                                          │
                                          ▼
                              ┌───────────────────────┐
                              │   MS-GESTAO-ALUNOS    │
                              │ ResultadoHistorico    │
                              │      Listener         │
                              └───────────────────────┘

                              ┌───────────────────────┐
                              │        EUREKA         │
                              │         :8761         │
                              │  Service Discovery    │
                              └───────────────────────┘
```

---

## 🔄 Comunicação Orientada a Eventos

Na versão anterior do projeto, o `ms-gestao-alunos` utilizava **OpenFeign** para realizar uma chamada HTTP diretamente ao `ms-historico`.

Nesta entrega, essa comunicação foi substituída por **mensageria assíncrona utilizando RabbitMQ**.

### Fluxo anterior

```text
ms-gestao-alunos
       │
       │ HTTP / Feign
       ▼
ms-historico
```

### Fluxo atual

```text
ms-gestao-alunos
       │
       │ Command
       ▼
    RabbitMQ
       │
       ▼
ms-historico
       │
       │ Result
       ▼
    RabbitMQ
       │
       ▼
ms-gestao-alunos
```

Dessa forma, os microsserviços não precisam realizar uma chamada HTTP direta para registrar o histórico.

---

## 🐇 RabbitMQ

O **RabbitMQ** é utilizado como intermediário na comunicação entre os microsserviços.

O projeto utiliza um **Topic Exchange** chamado:

```text
alunos.exchange
```

### Filas

| Fila                               | Responsabilidade                                 |
| ---------------------------------- | ------------------------------------------------ |
| `historico.registrar.queue`        | Recebe solicitações para registrar histórico     |
| `alunos.resultado-historico.queue` | Recebe o resultado do processamento do histórico |

### Routing Keys

| Routing Key            | Finalidade                                       |
| ---------------------- | ------------------------------------------------ |
| `historico.registrar`  | Solicita o registro de um histórico              |
| `historico.confirmado` | Informa que o histórico foi registrado           |
| `historico.recusado`   | Informa que o registro do histórico foi recusado |

---

## 📨 Padrões de Mensagens

A comunicação utiliza dois tipos principais de mensagens.

### RegistrarHistoricoCommand

Enviado pelo `ms-gestao-alunos` para solicitar o registro do histórico.

```java
public class RegistrarHistoricoCommand {

    private Long alunoId;
    private String descricao;
}
```

Exemplo:

```json
{
  "alunoId": 1,
  "descricao": "Aluno cadastrado"
}
```

---

### ResultadoHistorico

Enviado pelo `ms-historico` após o processamento da solicitação.

```java
public class ResultadoHistorico {

    private Long alunoId;
    private boolean confirmado;
    private String motivo;
}
```

Exemplo de confirmação:

```json
{
  "alunoId": 1,
  "confirmado": true,
  "motivo": null
}
```

Exemplo de recusa:

```json
{
  "alunoId": 1,
  "confirmado": false,
  "motivo": "Não foi possível registrar o histórico"
}
```

---

## 🔁 Fluxo de Cadastro de Aluno

Quando um novo aluno é cadastrado, o fluxo ocorre da seguinte forma:

```text
1. POST /alunos
       │
       ▼
2. AlunoService salva o aluno
       │
       ▼
3. HistoricoPublisher publica RegistrarHistoricoCommand
       │
       ▼
4. RabbitMQ recebe a mensagem
       │
       ▼
5. historico.registrar.queue
       │
       ▼
6. HistoricoListener recebe a mensagem
       │
       ▼
7. HistoricoService registra o histórico
       │
       ▼
8. ResultadoHistorico é publicado
       │
       ▼
9. RabbitMQ encaminha o resultado
       │
       ▼
10. ResultadoHistoricoListener recebe a confirmação
```

---

## 🧩 Componentes de Mensageria

### ms-gestao-alunos

```text
messaging/
├── HistoricoPublisher.java
├── RegistrarHistoricoCommand.java
├── ResultadoHistorico.java
├── ResultadoHistoricoListener.java
└── RabbitMQConfig.java
```

### ms-historico

```text
messaging/
├── HistoricoListener.java
├── RegistrarHistoricoCommand.java
├── ResultadoHistorico.java
└── RabbitMQConfig.java
```

### HistoricoPublisher

Responsável por publicar o comando de registro de histórico:

```java
rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE,
        RabbitMQConfig.ROUTING_KEY_REGISTRAR,
        comando
);
```

### HistoricoListener

Responsável por consumir o comando recebido pelo RabbitMQ e chamar o serviço responsável pelo registro do histórico.

### ResultadoHistoricoListener

Responsável por receber o resultado do processamento e informar se o histórico foi confirmado ou recusado.

---

## 📊 Vantagens da Arquitetura Orientada a Eventos

A utilização de comunicação assíncrona proporciona algumas vantagens:

- **Menor acoplamento:** os serviços não precisam realizar chamadas HTTP diretamente entre si.
- **Resiliência:** o produtor não depende de uma resposta imediata do consumidor.
- **Escalabilidade:** consumidores podem ser executados em múltiplas instâncias.
- **Processamento assíncrono:** operações podem continuar enquanto o processamento da mensagem ocorre.
- **Flexibilidade:** novos consumidores podem ser adicionados aos eventos sem alterar diretamente o produtor.
- **Desacoplamento temporal:** produtor e consumidor não precisam estar executando exatamente no mesmo momento para que a mensagem seja encaminhada pela infraestrutura de mensageria.

---

## ⚠️ Pontos de Atenção

A arquitetura orientada a eventos também adiciona alguns cuidados:

- O processamento deixa de ser imediatamente síncrono.
- É necessário monitorar filas e consumidores.
- Falhas de processamento precisam ser tratadas adequadamente.
- Mensagens podem exigir mecanismos de retry e controle de duplicidade em cenários mais complexos.
- O sistema passa a possuir mais componentes de infraestrutura.

Neste projeto, o RabbitMQ é utilizado como intermediário para organizar a comunicação entre os microsserviços.

---

## 🎯 Cenários de Uso

A comunicação orientada a eventos é adequada para operações que não precisam de uma resposta imediata do serviço consumidor.

No projeto, o registro do histórico é um exemplo desse cenário:

```text
Cadastro do aluno
       │
       ▼
Aluno salvo
       │
       ▼
Evento/comando enviado
       │
       ▼
Registro do histórico processado
```

O cadastro do aluno não precisa executar diretamente uma chamada HTTP para o serviço de histórico.

---

## 🛠 Tecnologias

- Java 17
- Spring Boot 4.0.7
- Spring Cloud 2025.1.2
- Spring Web (MVC)
- Spring Data JPA
- Spring AMQP
- RabbitMQ 4.3.6
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- H2 Database
- Maven
- Lombok
- JUnit 5

---

## 📁 Estrutura do Projeto

```text
TP4-Health_Fitness
│
├── eureka-server/                    # Servidor de descoberta de serviços
│   └── src/main/java/
│
├── api-gateway/                      # Gateway de roteamento
│   └── src/main/java/
│
├── ms-gestao-alunos/                 # Microsserviço de gestão de alunos
│   └── src/main/java/br/edu/infnet/ms_gestao_alunos/
│       ├── controllers/
│       ├── services/
│       ├── repositories/
│       ├── models/
│       └── messaging/
│           ├── HistoricoPublisher.java
│           ├── RegistrarHistoricoCommand.java
│           ├── ResultadoHistorico.java
│           ├── ResultadoHistoricoListener.java
│           └── RabbitMQConfig.java
│
├── ms-historico/                     # Microsserviço de histórico
│   └── src/main/java/br/edu/infnet/ms_historico/
│       ├── controllers/
│       ├── services/
│       ├── repositories/
│       ├── models/
│       └── messaging/
│           ├── HistoricoListener.java
│           ├── RegistrarHistoricoCommand.java
│           ├── ResultadoHistorico.java
│           └── RabbitMQConfig.java
│
└── README.md
```

---

## 🔗 Endpoints

### Alunos

`ms-gestao-alunos` — porta `8081`

| Método | Endpoint       | Descrição             |
| ------ | -------------- | --------------------- |
| GET    | `/alunos`      | Lista todos os alunos |
| GET    | `/alunos/{id}` | Busca aluno por ID    |
| POST   | `/alunos`      | Cadastra um aluno     |
| PUT    | `/alunos/{id}` | Atualiza um aluno     |
| DELETE | `/alunos/{id}` | Remove um aluno       |

### Histórico

`ms-historico` — porta `8082`

| Método | Endpoint               | Descrição                              |
| ------ | ---------------------- | -------------------------------------- |
| GET    | `/historico/{alunoId}` | Lista o histórico de um aluno          |
| POST   | `/historico`           | Registra uma nova entrada de histórico |

---

## 🐇 Configuração do RabbitMQ

O projeto utiliza uma instalação local do RabbitMQ.

### Configuração

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### Management UI

O RabbitMQ Management permite visualizar exchanges, filas, mensagens e consumidores.

```text
http://localhost:15672
```

---

## 🚀 Como executar

### Pré-requisitos

- Java 17
- Maven
- RabbitMQ
- Git

### 1. Clonar o projeto

```bash
git clone https://github.com/LeticiaFAAGomes/TP4-Health_Fitness.git
cd TP4-Health_Fitness
```

### 2. Iniciar o RabbitMQ

Verifique se o serviço RabbitMQ está em execução.

O Management UI pode ser acessado em:

```text
http://localhost:15672
```

### 3. Subir os serviços

#### Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

Aguarde o Eureka iniciar completamente.

#### Microsserviço de Histórico

```bash
cd ms-historico
mvn spring-boot:run
```

#### Microsserviço de Gestão de Alunos

```bash
cd ms-gestao-alunos
mvn spring-boot:run
```

#### API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

---

## 🧪 Teste da Comunicação Assíncrona

A comunicação pode ser validada através do Swagger.

### 1. Cadastrar um aluno

No `ms-gestao-alunos`:

```text
POST http://localhost:8081/alunos
```

Exemplo:

```json
{
  "nome": "Maria",
  "dataNascimento": "2000-05-10T00:00:00",
  "email": "maria@email.com",
  "telefone": "71999999999"
}
```

### 2. Verificar o recebimento no ms-historico

O console deve apresentar:

```text
Mensagem recebida pelo ms-historico
Alunos: 2
Descrição: Aluno cadastrado
```

### 3. Verificar o resultado no ms-gestao-alunos

Após o processamento, o console deve apresentar:

```text
Resultado do histórico recebido
Alunos: 2
Histórico Confirmado
```

### 4. Consultar o histórico

```text
GET http://localhost:8082/historico/2
```

O histórico registrado deverá aparecer na resposta.

Esse teste demonstra o fluxo completo:

```text
POST /alunos
     ↓
HistoricoPublisher
     ↓
RabbitMQ
     ↓
HistoricoListener
     ↓
HistoricoService
     ↓
Banco de dados
     ↓
ResultadoHistorico
     ↓
RabbitMQ
     ↓
ResultadoHistoricoListener
```

---

## 📋 Filas no RabbitMQ

Durante a execução da aplicação, as seguintes filas são criadas:

| Fila                               | Consumidor         |
| ---------------------------------- | ------------------ |
| `historico.registrar.queue`        | `ms-historico`     |
| `alunos.resultado-historico.queue` | `ms-gestao-alunos` |

O RabbitMQ Management permite acompanhar a quantidade de mensagens e consumidores conectados em cada fila.

---

## 🔄 Refatoração Realizada

A principal alteração desta entrega foi a substituição da comunicação síncrona baseada em **OpenFeign** pela comunicação assíncrona baseada em **RabbitMQ**.

### Antes

```text
ms-gestao-alunos
       │
       │ OpenFeign / HTTP
       ▼
ms-historico
```

### Depois

```text
ms-gestao-alunos
       │
       │ RabbitMQ
       ▼
ms-historico
       │
       │ Resultado
       ▼
ms-gestao-alunos
```

A responsabilidade de comunicação passou a ser concentrada na camada `messaging`, utilizando `RabbitTemplate`, `@RabbitListener`, exchanges, filas e routing keys.

---

## 📚 Conclusão

Nesta quarta entrega, o sistema de gestão de alunos foi refatorado para utilizar uma **Arquitetura Orientada a Eventos**, com RabbitMQ como message broker.

A comunicação entre `ms-gestao-alunos` e `ms-historico` deixou de depender de chamadas HTTP síncronas para o registro do histórico e passou a utilizar mensagens assíncronas.

Foram implementados:

- RabbitMQ como message broker;
- Exchange do tipo Topic;
- Filas para comandos e resultados;
- `RegistrarHistoricoCommand`;
- `HistoricoPublisher`;
- `HistoricoListener`;
- `ResultadoHistorico`;
- `ResultadoHistoricoListener`;
- Routing Keys para diferentes resultados;
- Conversão automática das mensagens para JSON utilizando Spring AMQP.

O fluxo foi validado através do cadastro de alunos, processamento do histórico, persistência no banco e recebimento do resultado pelo serviço de gestão de alunos.

---

## 👩‍💻 Autora

**Letícia Gomes**

Projeto desenvolvido para a disciplina **Desenvolvimento de Softwares Escaláveis**, aplicando conceitos de arquitetura de microsserviços, comunicação assíncrona, mensageria, RabbitMQ, Spring Boot, Spring Cloud e arquitetura orientada a eventos.

---

## 📄 Licença

Este projeto está licenciado sob a licença **MIT**.
