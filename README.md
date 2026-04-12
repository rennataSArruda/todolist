# Todo List API

API de uma aplicação de Todo List para gerenciamento de tarefas, construída com Spring Boot, Oracle, JPA e autenticação JWT.

## Objetivo

O projeto tem como objetivo servir como base para uma aplicação de lista de tarefas, com foco em:

- gerenciamento de usuários
- autenticação segura com JWT
- controle de sessão com refresh token
- proteção de rotas públicas e privadas

## Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Oracle Database
- Swagger / OpenAPI

## Requisitos

- Java 21
- Maven
- Oracle Database

## Configuração

O projeto usa:

- `src/main/resources/application.yaml` com placeholders
- `src/main/resources/application-local.yaml` com os valores locais

O arquivo `application-local.yaml` não deve ser commitado.

Existe um modelo em:

- `src/main/resources/application-local.example.yaml`

Exemplo:

```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:oracle:thin:@//localhost:1521/XEPDB1
    username: SEU_USUARIO
    password: SUA_SENHA

app:
  security:
    jwt:
      secret: SUA_CHAVE_JWT_COM_TAMANHO_ADEQUADO
      expiration-minutes: 15
      refresh-expiration-days: 7
      max-sessions: 1
    cleanup:
      cron: "0 0 * * * *"
```

Importante:

- o ambiente local deve subir com profile `dev`
- o Swagger/OpenAPI fica liberado apenas em `dev`
- fora de `dev`, `/v3/api-docs/**`, `/swagger-ui/**` e `/swagger-ui.html` ficam bloqueados pela configuração de segurança

## Executar

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

## Swagger

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

O botão `Authorize` usa o endpoint OAuth password flow em `/public/auth/token`.

Para a aplicação web, o endpoint oficial de login é:

- `POST /public/auth/login`

O endpoint abaixo não deve ser usado como contrato principal do frontend:

- `POST /public/auth/token`

Ele existe para suporte técnico ao Swagger/OpenAPI.

## Endpoints

### Públicos

- `GET /public/on`
- `POST /public/auth/login`
- `POST /public/auth/refresh`
- `POST /public/auth/logout`
- `POST /public/auth/token` - suporte ao Swagger/OpenAPI

### Privados

- `GET /api/usuario`
- `GET /api/usuario/{id}`
- `POST /api/usuario`
- `PUT /api/usuario/{id}`
- `POST /api/usuario/search`
- `POST /api/usuario/search-pagination`

## Autenticação

### Login

Este é o endpoint oficial para autenticação do frontend web.

```http
POST /public/auth/login
Content-Type: application/json
```

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Resposta:

```json
{
  "accessToken": "jwt",
  "refreshToken": "refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### Uso do token

```http
Authorization: Bearer <accessToken>
```

### Refresh

```http
POST /public/auth/refresh
Content-Type: application/json
```

```json
{
  "refreshToken": "refresh-token"
}
```

### Logout

```http
POST /public/auth/logout
Authorization: Bearer <accessToken>
Content-Type: application/json
```

```json
{
  "refreshToken": "refresh-token"
}
```

### Token OAuth para Swagger

```http
POST /public/auth/token
Content-Type: application/x-www-form-urlencoded
```

Uso recomendado:

- somente para o botão `Authorize` do Swagger/OpenAPI
- não usar como contrato principal do frontend web

## Segurança

- access token com expiração configurável
- refresh token persistido no banco
- rotação de refresh token no endpoint de refresh
- blacklist global para invalidar access token no logout
- limpeza automática de tokens expirados por agendamento
- limitação de sessões simultâneas por usuário

### Contrato de erro de autenticação

As falhas de autenticação/autorização protegidas pela camada de segurança retornam JSON no formato:

```json
{
  "status": 401,
  "code": "AUTH_TOKEN_EXPIRED",
  "action": "TOKEN_REFRESH",
  "message": "Token expirado",
  "path": "/api/usuario",
  "timestamp": "2026-04-12T00:00:00"
}
```

Campos:

- `status`: status HTTP efetivo
- `code`: código estável para tratamento no frontend
- `action`: ação recomendada para UX do frontend
- `message`: mensagem legível para log e exibição controlada
- `path`: rota solicitada
- `timestamp`: data/hora do erro

Contratos estabilizados:

- token expirado:
  - HTTP `401`
  - `code: AUTH_TOKEN_EXPIRED`
  - `action: TOKEN_REFRESH`
- token inválido:
  - HTTP `401`
  - `code: AUTH_TOKEN_INVALID`
  - `action: LOGIN`
- token invalidado em blacklist:
  - HTTP `401`
  - `code: AUTH_TOKEN_INVALIDATED`
  - `action: LOGIN`
- sessão revogada ou inválida:
  - HTTP `401`
  - `code: AUTH_SESSION_INVALID`
  - `action: LOGIN`
- sem permissão:
  - HTTP `403`
  - `code: AUTH_ACCESS_DENIED`
  - `action: FORBIDDEN`
- usuário sem perfil:
  - HTTP `403`
  - `code: AUTH_USER_WITHOUT_PROFILE`
  - `action: FORBIDDEN`

Leitura recomendada no frontend:

- `TOKEN_REFRESH`: tentar refresh de token antes de redirecionar
- `LOGIN`: limpar sessão local e redirecionar para login
- `FORBIDDEN`: manter sessão e exibir acesso negado

## Modelagem

Script SQL:

- `modelagem/01_usuario.sql`

O script contém:

- tabela `USUARIO`
- tabela `REFRESH_TOKEN`
- tabela `BLACKLISTED_TOKEN`
- insert do usuário default

Usuário default:

- username: `admin`
- password: `admin123`

## Postman

Collection disponível em:

- `postman/todolist.postman_collection.json`

## LOGIN ROOT
user: root
senha: admin123
