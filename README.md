# API SEID - Orchestrator

This project is a Spring Boot based orchestrator for the SEID (Sistema de Emergencias e Incidencias en Carreteras) for SEPE Plaiaundi. It integrates OpenData from Euskadi to provide real-time information about traffic cameras and incidences.

## Features

- **Dockerized**: Ready to run with Docker and Docker Compose.
- **REST API**: Complete CRUD operations for cameras, incidences, and users.
- **Real-time Sync**: Automated synchronization with OpenData Euskadi.
- **Security**: JWT-based authentication and authorization.
- **Mobile & Admin Flows**: Specific endpoints tailored for different clients.
- **Swagger Documentation**: Interactive API documentation and testing tools.

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)
- Maven (for local development)

## Quick Start (Docker)

To start the entire stack (API + MySQL Database):

```bash
docker compose up --build -d
```

The API will be available at `http://localhost:8080`.
The Swagger UI will be available at `http://localhost:8080/swagger-ui/index.html`.

## Local Development

If you prefer to run it locally:

1. Configure your database in `src/main/resources/application.properties`.
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Usage

### Authentication
Login to obtain a JWT token:
`POST /users/login` with `{"email": "...", "password": "..."}`.

Use the token in the `Authorization` header: `Bearer <token>`.

### Key Endpoints
- `GET /camara/mobile`: Active cameras for the mobile app.
- `GET /camara/admin`: All cameras with status management.
- `PATCH /camara/{id}/status`: Toggle camera status (Active/Disabled).
- `GET /incidencia?since=2024-01-29T10:00:00`: Delta updates for incidences.

## Authors
- SEPE Plaiaundi