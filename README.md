# Autoflex Backend

Production-ready backend for inventory management and production suggestions.

## Tech Stack

- Java 21 (LTS)
- Spring Boot 3.5.x
- Maven
- MySQL
- Spring Data JPA / Hibernate
- Flyway
- Lombok
- MapStruct
- Bean Validation
- OpenAPI (Swagger UI)
- JUnit 5 + MockMvc + Testcontainers

## Project Goal

The system manages products and raw materials, associates raw materials to products, and calculates production suggestions based on available stock.

Business rule:

`possibleProduction = min(rawMaterial.stockQuantity / requiredQuantity)`

Suggestions are sorted by highest `totalValue` and include a summary with `totalProductionValue`.

## Architecture

Layered architecture:

- `controller`
- `service`
- `repository`
- `entity`
- `dto`
- `mapper`
- `exception`
- `config`

## Main Features

- CRUD for products
- CRUD for raw materials
- Add/remove raw material associations for products
- Production suggestion endpoint with summary value
- Global exception handling
- Request/response DTOs
- Validation annotations
- Proper HTTP status codes
- Pagination support
- OpenAPI documentation

## Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+
- Docker Desktop (required for Testcontainers integration tests)

## Environment Variables

Optional (defaults are configured in `application.yml`):

- `DB_URL` (default: `jdbc:mysql://localhost:3306/autoflex?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD` (default: `root`)
- `SERVER_PORT` (default: `8080`)

## Database Migrations

Flyway is enabled and runs automatically at startup.

- Migration location: `src/main/resources/db/migration`
- Runtime migration location: `src/main/resources/db/migration/mysql`
- Runtime migration: `V1__init_mysql_schema.sql`
- PostgreSQL migrations (`V1`, `V2`, `V3`) are kept for historical/reference context in docs.

Hibernate is configured with:

- `spring.jpa.hibernate.ddl-auto=validate`

## Database Architecture Package

Professional SQL package is available in:

- `docs/database/01_create_schema.sql`
- `docs/database/02_seed_sample_data.sql`
- `docs/database/03_index_optimization.sql`
- `docs/database/04_conventions_and_integration.md`
- `docs/database/05_production_queries.sql`
- `docs/database/06_verification_checklist.md`

## Running the Application

```bash
mvn spring-boot:run
```

## API Documentation

After starting the app:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## REST Endpoints

### Products

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`
- `POST /api/products/{productId}/raw-materials`
- `DELETE /api/products/{productId}/raw-materials/{rawMaterialId}`

### Raw Materials

- `GET /api/raw-materials`
- `GET /api/raw-materials/{id}`
- `POST /api/raw-materials`
- `PUT /api/raw-materials/{id}`
- `DELETE /api/raw-materials/{id}`

### Production

- `GET /api/production/suggestions`

## Example Requests

### Create Product

```http
POST /api/products
Content-Type: application/json

{
  "code": "PRD-100",
  "name": "Premium Widget",
  "price": 199.90
}
```

### Create Raw Material

```http
POST /api/raw-materials
Content-Type: application/json

{
  "code": "RM-STEEL",
  "name": "Steel",
  "stockQuantity": 1000.0000
}
```

### Associate Raw Material to Product

```http
POST /api/products/{productId}/raw-materials
Content-Type: application/json

{
  "rawMaterialId": 1,
  "requiredQuantity": 10.0000
}
```

### Get Production Suggestions

```http
GET /api/production/suggestions
```

## Testing

Run all tests:

```bash
mvn test
```

Current automated test coverage includes:

- Unit tests for production suggestion calculation
- Integration tests for product endpoints
- Integration tests for raw material endpoints
- Integration tests for production suggestion endpoint
- Integration tests for association and common REST error scenarios (`400`, `404`, `409`)

## CI Pipeline

GitHub Actions workflow is available at:

- `.github/workflows/ci.yml`

It runs on push and pull request to `main` and executes:

- `mvn -B -ntp clean test`

## Postman Collection

A ready-to-import Postman collection is available at:

- `docs/autoflex-api.postman_collection.json`

Collection variables:

- `baseUrl` (default: `http://localhost:8080`)
- `productId`
- `rawMaterialId`

## Notes

- Pagination uses stable DTO serialization for consistent API output.
- Global error response includes validation details when applicable.
- Build artifacts are excluded via `.gitignore`.
