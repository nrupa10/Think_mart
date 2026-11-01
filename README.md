# ThinkMart

ThinkMart is a full-stack e-commerce demo built with Spring Boot 3, Java 21, Spring Security, JPA, MySQL, and a vanilla HTML/CSS/JavaScript storefront. Spring Boot serves the frontend and JSON API from one origin on port 8080.

## Requirements

- Java 21
- Maven 3.9+
- MySQL 8+

## Run locally

1. Start MySQL. The configured database name is `thinkmart_db`; the JDBC URL creates it automatically when the MySQL account has database-creation privileges. Otherwise, create it once with `CREATE DATABASE thinkmart_db;`.
2. From PowerShell, set the required variables. Generate a fresh signing key rather than reusing the example below:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$rng.Dispose()
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-mysql-password"
```

3. Optionally create a local admin during startup. The password must have at least 12 characters; it is BCrypt-hashed before storage. Pick a private local password and set it only in your shell:

```powershell
$env:ADMIN_EMAIL = "admin@thinkmart.local"
$env:ADMIN_PASSWORD = "your-private-password-of-12-or-more-characters"
```

The sample admin email above is created only when both `ADMIN_EMAIL` and `ADMIN_PASSWORD` are set and the email does not already exist. There is no built-in admin password. If you skip these variables, public registration creates regular USER accounts only.

4. Start the application from the backend directory:

```powershell
cd backend
mvn spring-boot:run
```

5. Open [http://localhost:8080](http://localhost:8080). The login page is the root page; select **Create an account** to register a customer account. Sign in with the admin email and the password you set in step 3 to exercise admin-only product write APIs.

The database schema is created/updated by Hibernate. On first startup, ThinkMart seeds five categories and 15 products; seeding is idempotent by category and product slug. Product images, Google Fonts, and Font Awesome are served from their public CDNs, so those visual assets need an internet connection.

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `PORT` | `8080` | HTTP port |
| `DB_URL` | local MySQL `thinkmart_db` URL | JDBC connection URL |
| `DB_USERNAME` | `root` | MySQL user |
| `DB_PASSWORD` | empty | MySQL password |
| `JWT_SECRET` | required | Base64-encoded random key; at least 32 decoded bytes |
| `JWT_EXPIRATION_MS` | `86400000` | JWT lifetime in milliseconds |
| `ADMIN_EMAIL` | unset | Optional admin account email |
| `ADMIN_PASSWORD` | unset | Optional admin password, BCrypt-hashed at initialization |
| `ADMIN_NAME` | `ThinkMart Admin` | Optional admin display name |

`application.properties` reads credentials from environment variables. Do not commit credentials or JWT secrets. For a frontend served from a separate local development port, CORS permits localhost and `127.0.0.1`; production should use same-origin hosting and an explicit production CORS policy.

## API

All request and response bodies use JSON. Validation errors return `400` with a message and field errors; a duplicate email returns `409`; unauthenticated requests return `401`; role failures return `403`. Protected endpoints require `Authorization: Bearer <token>`.

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Public | Register and return a JWT and public user profile |
| `POST` | `/api/auth/login` | Public | Authenticate and return a JWT and public user profile |
| `GET` | `/api/auth/me` | Authenticated | Current public user profile |
| `GET` | `/api/products` | Public | Browse; optional `query`, `category`, and `sort` (`price-asc`, `price-desc`, `rating`, `newest`) |
| `GET` | `/api/products/categories` | Public | List categories |
| `GET` | `/api/products/{id}` | Public | Product detail |
| `GET` | `/api/products/slug/{slug}` | Public | Product detail by slug |
| `POST` | `/api/products` | ADMIN | Create product |
| `PUT` | `/api/products/{id}` | ADMIN | Update product |
| `DELETE` | `/api/products/{id}` | ADMIN | Delete product |
| `GET` | `/api/cart` | Authenticated | Read the current user's persistent cart |
| `POST` | `/api/cart/items` | Authenticated | Add `{ "productId": 1, "quantity": 1 }` |
| `PUT` | `/api/cart/items/{itemId}` | Authenticated | Set `{ "quantity": 2 }` |
| `DELETE` | `/api/cart/items/{itemId}` | Authenticated | Remove an item |
| `POST` | `/api/orders` | Authenticated | Create an order from the server-side cart and current database prices |
| `GET` | `/api/orders/my-orders` | Authenticated | List the current user's orders |
| `GET` | `/api/orders/{id}` | Authenticated | Read an owned order; other users' orders are not exposed |
| `POST` | `/api/contact` | Public | Validate and persist a contact message |

### Example payloads

Registration:

```json
{
  "fullName": "Jordan Taylor",
  "email": "jordan@example.com",
  "phone": "+1 (555) 010-1234",
  "password": "a-private-password"
}
```

Order placement (the frontend never submits product prices or totals):

```json
{
  "customerName": "Jordan Taylor",
  "customerEmail": "jordan@example.com",
  "customerPhone": "+1 (555) 010-1234",
  "deliveryAddress": "123 Market Street, San Francisco, CA 94105"
}
```

The checkout payment step is explicitly a simulation: it records a placed order and confirmation without collecting card data or charging a payment method.

## Project layout

```text
frontend/                 Static pages, styles, JavaScript, and assets
backend/pom.xml           Maven build and frontend packaging
backend/src/main/java/    Spring Boot API, services, entities, and security
backend/src/main/resources/application.properties
```
