Desafio Desenvolvedor - Plataforma de Gestão Acadêmica
Este projeto é uma aplicação web responsiva desenvolvida para o desafio de desenvolvedor da UNIFOR. O sistema visa permitir a administração de alunos, professores e cursos, com diferentes níveis de acesso e funcionalidades para cada tipo de usuário. 

Tabela de Conteúdos
Arquitetura e Padrões de Projeto

Tecnologias Utilizadas

Como Executar a Aplicação

Decisões Técnicas e Suposições

Arquitetura e Padrões de Projeto
A solução foi desenvolvida seguindo um conjunto de padrões de arquitetura e de projeto para garantir manutenibilidade, clareza e escalabilidade, conforme os critérios de avaliação. 

Padrões de Arquitetura (Alto Nível)
Arquitetura Cliente-Servidor (Client-Server)
Conforme a exigência de desenvolver frontend e backend de forma separada, o projeto adota o modelo Cliente-Servidor. O 

Frontend (Angular) atua como o cliente, responsável pela interface e experiência do usuário, enquanto o Backend (Java/Quarkus) funciona como o servidor, responsável pela lógica de negócio e persistência dos dados.

API RESTful
A comunicação entre o cliente e o servidor é realizada através de uma API RESTful. O backend expõe recursos (como usuários, cursos, etc.) que são manipulados pelo frontend utilizando os verbos padrão do protocolo HTTP (GET, POST, PUT, DELETE).

Autenticação Centralizada
Seguindo o requisito de segurança, a arquitetura utiliza um provedor de identidade centralizado, o 

Keycloak. Nenhuma aplicação (frontend ou backend) gerencia senhas diretamente. Elas delegam a tarefa de autenticação ao Keycloak, que emite tokens de acesso para os clientes autorizados.

Padrões de Código (Backend)
Arquitetura em Camadas (Layered Architecture)
O código do backend foi estruturado em três camadas lógicas para separar as responsabilidades:

Camada de Recurso (Resource/Controller): Classes JAX-RS (@Path) que expõem os endpoints da API e lidam com as requisições e respostas HTTP.

Camada de Serviço (Service): Classes que contêm a lógica de negócio principal, validações e orquestração das operações.


Camada de Dados (Repository/Entity): Entidades JPA (@Entity) que mapeiam o banco de dados relacional  e repositórios (via Panache) que abstraem o acesso aos dados.

Injeção de Dependência (Dependency Injection - DI)
Utilizamos o mecanismo de injeção de dependências do Quarkus (CDI) através da anotação @Inject. Esse padrão remove o acoplamento direto entre os componentes (ex: um Resource não precisa instanciar um Service), o que torna o código mais modular, flexível e fácil de testar.

Data Transfer Object (DTO)
Para a comunicação entre o cliente e o servidor, foram utilizados DTOs. Estes são objetos simples que carregam apenas os dados necessários para uma determinada visão, evitando a exposição de detalhes internos das entidades do banco de dados e otimizando o tráfego de dados.

Padrões de Segurança
Autenticação Baseada em Token (JWT)
O fluxo de segurança é baseado em JSON Web Tokens (JWT). Após o login bem-sucedido no Keycloak, o frontend recebe um JWT, que é enviado no cabeçalho de cada requisição à API. O backend (configurado como um Resource Server) valida este token para garantir a autenticidade e as permissões do usuário antes de processar a requisição.

Padrões de Infraestrutura e Implantação
Containerização
Tanto o frontend quanto o backend foram empacotados em imagens 

Docker. Esse padrão garante que as aplicações rodem em um ambiente consistente e isolado, simplificando a implantação e eliminando problemas de "funciona na minha máquina".

Infraestrutura como Código (Infrastructure as Code - IaC)
Utilizamos o 

Docker Compose para definir e orquestrar toda a pilha de serviços da aplicação (backend, banco de dados, Keycloak) em um único arquivo declarativo (docker-compose.yml). Isso permite que todo o ambiente de desenvolvimento seja recriado de forma rápida e confiável com um único comando.

Tecnologias Utilizadas
Backend: Java 17, Quarkus


Frontend: Angular 15+ (Standalone) 


Banco de Dados: PostgreSQL (ou outro banco relacional) 


Segurança: Keycloak 


Containerização: Docker e Docker Compose 


Versionamento: Git 

Como Executar a Aplicação
Pré-requisitos:

Git

Docker

Docker Compose

Passos:

Clone o repositório:

Bash

git clone https://github.com/Ernilson/desafio_dev_senior/
Navegue até a raiz do projeto:

Bash

cd src/main/docker
Suba os containers:

Bash

docker-compose up --build
Acesse as aplicações:

Frontend: http://localhost:4200

Backend (API): http://localhost:8080

Keycloak Admin Console: http://localhost:8081

Decisões Técnicas
Aqui estão as principais decisões de arquitetura e tecnologia tomadas para atender aos requisitos do projeto.

Arquitetura Cliente-Servidor com API RESTful

Decisão: Optou-se por uma arquitetura desacoplada, com um backend servindo uma API RESTful e um frontend consumindo essa API.


Justificativa: Esta decisão atende diretamente ao requisito de "Desenvolver os módulos de frontend e backend (API Rest) de forma separada". Ela promove a independência das equipes, facilita a manutenção e permite que a API seja consumida por outros clientes no futuro.

Tecnologias do Backend: Java e Quarkus

Decisão: O backend foi desenvolvido utilizando a linguagem Java com o framework Quarkus.


Justificativa: Escolha direta para cumprir a exigência explícita do desafio. O Quarkus foi selecionado por sua alta performance, baixo consumo de memória e excelente integração com o ecossistema de containers (Docker).

Gestão de Segurança com Keycloak

Decisão: Toda a gestão de autenticação e autorização foi delegada a um serviço centralizado, o Keycloak.


Justificativa: Atende ao requisito de que a aplicação seja segura e tenha suas políticas de acesso gerenciadas pelo Keycloak. Esta abordagem aumenta a segurança ao separar as responsabilidades, permitindo o uso de padrões robustos como OAuth 2.0/OIDC e facilitando a implementação de funcionalidades como Single Sign-On (SSO).

Containerização e Orquestração com Docker

Decisão: Todo o ambiente da aplicação (backend, banco de dados, Keycloak) é definido e gerenciado através de Docker e Docker Compose.


Justificativa: Cumpre o requisito de utilizar Docker como padrão para orquestrar a aplicação e é um diferencial explícito. Essa abordagem garante um ambiente de desenvolvimento e implantação consistente, simplifica o setup e facilita a distribuição da aplicação.


Estrutura do Código Backend: Arquitetura em Camadas

Decisão: O código do backend foi organizado em camadas (Resource, Service, Repository/Entity).


Justificativa: Embora não seja um requisito explícito, esta é uma boa prática que visa atender aos critérios de "manutenibilidade, clareza e limpeza de código". A separação de responsabilidades torna o código mais fácil de entender, testar e evoluir.

Suposições Realizadas
Estas são as suposições feitas para preencher lacunas nos requisitos e permitir o desenvolvimento.

Mapeamento de Visões para Papéis (Roles)


Suposição: Assumiu-se que as "Visões" descritas no desafio (administrador, coordenador, professor e aluno)  podem ser diretamente mapeadas para papéis de usuário (


roles) dentro do Keycloak e da aplicação. Foi criado um único tipo de usuário no sistema, cujo acesso é determinado pelo papel que lhe é atribuído.

Estrutura da Matriz Curricular


Suposição: O requisito de "Realizar a montagem da matriz curricular"  foi interpretado como a capacidade de associar um conjunto de disciplinas a um curso em um determinado semestre. Assumiu-se que uma entidade de ligação (

MatrizCurricular) que relaciona Curso, Disciplina e Semestre seria suficiente para esta funcionalidade, não sendo necessário implementar regras complexas como pré-requisitos entre disciplinas.

Modelo de Dados Básico


Suposição: Como os atributos específicos das entidades (alunos, professores, cursos, etc.)  não foram detalhados, assumiu-se um modelo de dados mínimo e essencial para a aplicação funcionar. Por exemplo, um usuário possui nome e email, e um curso possui um nome.

Ambiente Unificado via Docker Compose

Suposição: Assumiu-se que o ambiente de desenvolvimento e avaliação será executado inteiramente através do Docker Compose. Isso implica que o avaliador não precisará instalar localmente Java, Maven, Node.js ou PostgreSQL, necessitando apenas do Docker e Docker Compose, simplificando radicalmente o processo de "rodar a aplicação".

/unifor/
  ├── docker-compose.yml
  ├── /backend/
  └── /frontend/  <-- O Dockerfile deve estar AQUI DENTRO