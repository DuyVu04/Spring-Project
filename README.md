# Fast English Backend

This is the Spring Boot backend for the Fast English project. It provides APIs and services for authentication, course management, lesson learning, chat, vocabulary, payment, attendance, and administration.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- MySQL
- Redis
- MinIO
- Docker Compose

## Prerequisites

Before running the project, make sure you have:

- Java 21
- Maven
- Docker
- Docker Compose

## Environment Setup

This project uses environment variables for sensitive configuration.

The repository includes [`.env.example`](E:\PRO-FINALYEAR\spring-project\.env.example) as a template, but does not commit the real `.env` file.

### Create `.env`

Copy `.env.example` to `.env` and update the values if needed.

Example:

```bash
cp .env.example .env
```

On Windows, you can create `.env` manually based on `.env.example`.

## Run with Docker

From the [spring-project](E:\PRO-FINALYEAR\spring-project) directory, run:

```bash
mvn clean package -DskipTests
docker compose up --build
```

This will:

- build the Spring Boot jar
- start MySQL
- start Redis
- start MinIO
- start the backend API

## Default Service Ports

- Backend API: `http://localhost:8080/api`
- MySQL: `localhost:3306`
- Redis: `localhost:6379`
- MinIO API: `http://localhost:9000`
- MinIO Console: `http://localhost:9001`

## Notes

- The current Docker setup expects the application jar to exist in `target/`, so `mvn clean package -DskipTests` must be run before `docker compose up --build`.
- The backend reads its database, Redis, MinIO, JWT, mail, and payment configuration from environment variables defined in `.env`.
- If you change the `.env` file, rebuild and restart the containers.

## Stop the Services

```bash
docker compose down
```

If you also want to remove volumes:

```bash
docker compose down -v
```