# 🌊 Projeto +Água

O projeto **+Água** tem como objetivo monitorar e prevenir os impactos causados por eventos extremos, como **enchentes** e **secas**, por meio de uma solução tecnológica inovadora. A proposta integra sensores de monitoramento de reservatórios com uma **API RESTful desenvolvida em Spring Boot**, que centraliza os dados e aplica regras de negócio para tomada de decisão eficiente.

---

## 🧠 Contexto

Diante do aumento de eventos extremos relacionados à água, como alagamentos urbanos e crises hídricas, torna-se essencial uma solução tecnológica que permita o **monitoramento constante dos níveis de água**, qualidade e alertas de risco para gestão preventiva em condomínios e regiões urbanas.

---

## 🛠 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.4.6**
- **Spring Data JPA**
- **Spring Security com JWT**
- **Bean Validation (Jakarta Validation)**
- **Banco de Dados Oracle**
- **Swagger OpenAPI (SpringDoc)**
- **Lombok**
- **Insomnia** (para testes)
- **Render e Dockerfile** (para deploy)

---

## 🔐 Autenticação

A API utiliza **JWT (JSON Web Token)** para autenticação. Após o login, um token é gerado e deverá ser utilizado no header `Authorization` para as requisições autenticadas.

---

## 📂 Estrutura de Pastas

```
src
└── main
    └── java
        └── br.com.fiap.mais_agua
            ├── config               # Configurações de segurança 
            ├── controller           # Controllers REST da API
            ├── exception            # Tratamento de exceções (global e validações)
            ├── model
            │   ├── DTO              # Data Transfer Objects
            │   └── (entities)       # Entidades do banco
            ├── repository           # Interfaces JPA
            ├── service              # Regras de negócio
            └── specification        # Filtros dinâmicos para consultas (JPA Specification)
```

---

## 🔁 Principais Funcionalidades da API

- Cadastro e login de usuários
- Cadastro de unidades e seus reservatórios
- Associação automática de dispositivos (ESP32, acoplados com sensores) aos reservatórios
- Armazenamento histórico do nível da água
- Registro de status da água (Cheio, Normal, Crítico...)
- Leitura de sensores (nível, turbidez, pH)
- Filtros dinâmicos por reservatório nas leituras
- Documentação Swagger disponível

---

## 📘 Documentação da API

Acesse via navegador:

```
http://localhost:8080/swagger-ui/index.html
```

---

## ▶ Instruções de Uso

1. Clone o projeto
2. Configure o `application.properties` com seu Oracle DB
3. Rode o projeto com Spring Boot
4. Utilize `/login` para gerar token JWT
5. Use o token nas demais requisições autenticadas

---

## 🌐 Deploy (em nuvem)

- [Link da API em Produção](https://maisagua-api.onrender.com)
- [Link do repositório GitHub](https://github.com/MariaEdPaixao/MaisAgua-API.git)
- [Pitch do Projeto (até 3min)](https://www.youtube.com/watch?v=sSYWvdgidYY)

---

## 👨‍💻 Membros do Grupo

- **Laura de Oliveira Cintra** - RM558843  
- **Maria Eduarda Alves da Paixão** - RM558832  
- **Vinicius Saes de Souza** - RM554456

---

## 📌 Observações

A API foi construída para fornecer dados para o app mobile +Água, que permite visualizar informações em formato dashboard, permitindo acesso seguro e em tempo real aos dados monitorados dos reservatórios.

---

Feito com 💙 pelo grupo +Água.
