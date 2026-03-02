# Authentication and Authorization Validation Checklist

## Setup

- [ ] Run backend with security enabled (`SECURITY_ENABLED=true`)
- [ ] Confirm default users are seeded (`admin/admin123`, `user/user123`)
- [ ] Confirm Swagger UI is available at `/swagger-ui.html`

## Authentication

- [ ] `POST /auth/login` returns `200` and JWT for valid credentials
- [ ] `POST /auth/login` returns `401` for invalid credentials
- [ ] `POST /auth/register` creates a USER account with hashed password
- [ ] Registering an existing username returns `409`

## Authorization Rules

- [ ] Requests without JWT to `/api/**` return `401`
- [ ] `ADMIN` can access product CRUD endpoints
- [ ] `ADMIN` can access raw material CRUD endpoints
- [ ] `ADMIN` can manage product/raw-material associations
- [ ] `USER` cannot access product or raw material CRUD (`403`)
- [ ] `USER` can access `GET /api/production/suggestions`

## Token Validation

- [ ] Expired token is rejected (`401`)
- [ ] Malformed token is rejected (`401`)
- [ ] Token with unknown user is rejected (`401`)

## Swagger JWT Testing

- [ ] Use `Authorize` button with `Bearer <token>`
- [ ] Execute protected endpoints successfully according to role

## Regression Checks

- [ ] Existing business endpoints remain functional with valid JWT
- [ ] CORS preflight from `http://localhost:*` succeeds for frontend
