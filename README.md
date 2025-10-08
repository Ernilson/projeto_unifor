### 🧩 **Desafio Desenvolvedor UNIFOR – Plataforma de Gestão Acadêmica**

#### **📐 Arquitetura Geral**

A solução adota uma **arquitetura cliente-servidor desacoplada**, composta por:

* **Frontend (Angular 15+)** — interface web responsiva, responsável pela interação com o usuário.
* **Backend (Java 21 + Quarkus)** — responsável pela lógica de negócio, validações e persistência.
* **Banco de Dados (PostgreSQL)** — camada de persistência relacional.
* **Keycloak** — provedor de identidade centralizado (autenticação/autorização).
* **Docker + Docker Compose** — para orquestração e execução unificada de todos os serviços.

Essa estrutura garante **isolamento**, **reprodutibilidade** e **escalabilidade**.

---

#### **🏗️ Padrões e Decisões Técnicas**

1. **Arquitetura em Camadas (Layered Architecture)**
   O backend segue três camadas principais:

   * **Resource/Controller:** expõe os endpoints REST (@Path)
   * **Service:** contém a lógica de negócio
   * **Repository/Entity:** mapeamento JPA e acesso ao banco via Panache

2. **API RESTful**
   A comunicação entre Angular e Quarkus ocorre por meio de uma **API RESTful**, usando os verbos HTTP padrão (GET, POST, PUT, DELETE).

3. **Injeção de Dependência (CDI)**
   Quarkus fornece o container CDI (@Inject), que desacopla componentes e melhora a testabilidade.

4. **Data Transfer Object (DTO)**
   Os DTOs padronizam a troca de dados entre cliente e servidor, garantindo segurança e redução de payloads.

5. **Segurança com Keycloak + JWT**

   * O **Keycloak** gerencia autenticação e papéis de usuário (admin, coordenador, professor, aluno).
   * O **backend** valida tokens JWT em cada requisição.
   * Implementa os padrões **OAuth2** e **OpenID Connect (OIDC)**.

6. **Containerização e Infraestrutura como Código (IaC)**

   * Cada componente (frontend, backend, banco, Keycloak) roda em um container isolado.
   * O arquivo **`docker-compose.yml`** define todo o ambiente (infraestrutura declarativa).
   * Com um único comando (`docker compose up`), todo o ecossistema sobe automaticamente.

7. **Decisões Técnicas Principais**

   * **Framework:** Quarkus, pela performance e integração nativa com Docker.
   * **Banco:** PostgreSQL, por sua robustez e compatibilidade com Quarkus.
   * **Autenticação:** Delegada ao Keycloak, evitando gestão de senhas no código.
   * **Infraestrutura:** Totalmente orquestrada via Docker Compose.

---

#### **📦 Padrões Implementados**

| Categoria      | Padrão                                  | Descrição                            |
| -------------- | --------------------------------------- | ------------------------------------ |
| Arquitetura    | Cliente-Servidor                        | Separação entre Angular e Quarkus    |
| Código         | Camadas (Resource, Service, Repository) | Clareza e manutenibilidade           |
| Segurança      | JWT + OAuth2                            | Tokens validados no backend          |
| Infraestrutura | Docker + Compose                        | Reprodutibilidade e isolamento       |
| Comunicação    | RESTful API                             | Interação entre frontend e backend   |
| Autenticação   | Keycloak                                | Centralização de identidade e papéis |
| Deploy         | IaC (Docker Compose)                    | Configuração automática do ambiente  |

---

#### **🔐 Fluxo de Autenticação com Keycloak**

1. O usuário acessa o **Angular** e é redirecionado ao **Keycloak**.
2. Após login, o **Keycloak** emite um **JWT**.
3. O token é enviado em cada requisição HTTP ao **backend Quarkus**.
4. O Quarkus valida o token e libera o acesso conforme o papel do usuário.

---

#### **🐳 Arquitetura de Containers (Docker Compose)**

```
┌─────────────────────────────────────────────────────────────┐
│                       DOCKER COMPOSE                        │
│                                                             │
│ ┌──────────────┐   ┌─────────────────┐   ┌───────────────┐  │
│ │  FRONTEND    │ → │   API (Quarkus) │ → │  POSTGRES DB  │  │
│ │  Angular 15+ │   │  Java 17 + JPA  │   │  Persistência │  │
│ └──────────────┘   └─────────────────┘   └───────────────┘  │
│          │                         ▲                        │
│          ▼                         │                        │
│      ┌──────────────┐              │                        │
│      │  KEYCLOAK    │ ─────────────┘                        │
│      │ Auth Server  │ (emite JWT / gerencia papéis)         │
│      └──────────────┘                                       │
└─────────────────────────────────────────────────────────────┘
```

---

#### **🧠 Principais Suposições**

* As visões de usuário (Administrador, Coordenador, Professor, Aluno) foram mapeadas em **roles** do Keycloak.
* A **Matriz Curricular** associa **Curso**, **Disciplina** e **Semestre**, sem complexidades como pré-requisitos.
* O ambiente completo roda via **Docker Compose**, dispensando instalação de dependências locais.

---

#### **⚙️ Como Executar**

```bash
# Clonar o projeto
git clone https://github.com/Ernilson/desafio_dev_senior/

# Acessar a pasta Docker
cd projeto_unifor/unifor

# Subir todo o ambiente
docker compose up --build

# No navegador
http://localhost:4200/home
```

* **Frontend:** [http://localhost:4200/home](http://localhost:4200/home)
* **Backend (API):** [http://localhost:8080](http://localhost:8080)
* **Keycloak Admin Console:** [http://localhost:8081](http://localhost:8081)

---


