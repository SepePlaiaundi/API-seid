# Technical Documentation - API SEID

## Architecture

The project follows a layered architecture to ensure separation of concerns and maintainability.

### 1. Presentation Layer (REST Controllers)
- Handles incoming HTTP requests.
- Uses DTOs for data transfer.
- Implements specific logic for different clients (Mobile vs Admin).

### 2. Business Logic Layer (Services)
- Contains the core logic of the application.
- Handles synchronization with external APIs using `RestClient`.
- Implements coordinate normalization using `proj4j`.
- Manages entity transitions and business rules (e.g., Soft Delete, Status Toggle).

### 3. Data Access Layer (Repositories)
- Uses Spring Data JPA for persistence.
- Implements custom queries for performance and specific logic.
- Supports MariaDB/MySQL in production and H2 in testing.

### 4. Infrastructure Layer
- **Security**: JWT implementation with custom filters and `AuthenticationManager`.
- **OpenAPI**: Swagger configuration for documentation and JWT handling.
- **Mappers**: Conversion between Entities and DTOs using manual/MapStruct-style patterns.

## Data Synchronization Flow

1. **Trigger**: Manual POST to `/{target}/sync` or via scheduled workers.
2. **Download**: Multiple pages are downloaded in parallel using `CompletableFuture`.
3. **MAPPING**: External DTOs are mapped to internal entities.
4. **Validation**: Geographic data and resource sanity checks.
5. **Persist**: Batch update logic that detects existing records to perform UPSERT operations.

## Security Model

- **Authentication**: Stateless JWT.
- **Token Generation**: Claims include user identification and roles.
- **Authorization**: Role-based access control (RBAC) enforced via Spring Security annotations.
- **Swagger Integration**: Enabled JWT Bearer authentication within the UI for developer testing.

## Docker Setup

The `Dockerfile` uses a multi-stage build to minimize the final image size:
- **Build Stage**: Uses Maven to compile and package the app.
- **Run Stage**: Uses a lightweight JRE image to execute the jar.

The `compose.yaml` file defines the bridge network and service dependencies, ensuring the database is healthy before the API starts.
