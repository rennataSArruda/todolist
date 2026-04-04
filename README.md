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

## Endpoints

### Públicos

- `GET /public/on`
- `POST /public/auth/login`
- `POST /public/auth/refresh`
- `POST /public/auth/logout`
- `POST /public/auth/token`

### Privados

- `GET /api/usuario`
- `GET /api/usuario/{id}`
- `POST /api/usuario`
- `PUT /api/usuario/{id}`
- `POST /api/usuario/search`
- `POST /api/usuario/search-pagination`

## Autenticação

### Login

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

## Segurança

- access token com expiração configurável
- refresh token persistido no banco
- rotação de refresh token no endpoint de refresh
- blacklist global para invalidar access token no logout
- limpeza automática de tokens expirados por agendamento
- limitação de sessões simultâneas por usuário

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
