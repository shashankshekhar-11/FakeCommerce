# FakeCommerce

A Spring Boot e-commerce backend exposing REST APIs for categories, products, orders (with cart-like item management), and product reviews — backed by MySQL, versioned with Flyway, and instrumented with New Relic.

## Tech Stack

- **Java 21**, **Spring Boot 4.0.2** (Web MVC, Spring Data JPA)
- **MySQL 8** as the datastore
- **Flyway** for schema migrations
- **Lombok** for boilerplate (`@Data`, `@Builder`, etc.)
- **Gradle** (wrapper included, no local install needed)
- **New Relic Java Agent** for APM (optional, disabled by default without a license key)
- **Spring Boot DevTools** for hot reload during development

## Features

- Soft deletes across all entities (`deleted_at` column + Hibernate `@SQLDelete`/`@SQLRestriction`) — records are never hard-deleted, just excluded from future queries.
- Auditing timestamps (`created_at`, `updated_at`) on every entity via `BaseEntity` + `@EntityListeners(AuditingEntityListener.class)`.
- Consistent JSON envelope for every response via `ApiResponse<T>` (`success`, `message`, `error`, `data`).
- Centralized exception handling (`GlobalExceptionHandler`) mapping domain exceptions to HTTP status codes (404, 400, 409, 500).
- Cart-style order item management (`ADD` / `REMOVE` / `INCREMENT` / `DECREMENT`) on an existing order.
- Product ↔ Category relations, Order ↔ Product line items, and Review ↔ Order/Product relations.

## Domain Model

| Entity | Table | Notes |
|---|---|---|
| `Category` | `categories` | `name` |
| `Product` | `products` | `title`, `description`, `price`, `image`, `rating`, belongs to a `Category` |
| `Order` | `orders` | `status` (`PENDING`, ...), has many `OrderProducts` |
| `OrderProducts` | `order_products` | Join entity: `order`, `product`, `quantity` |
| `Review` | `reviews` | `comment`, `rating`, tied to one `Order` and one `Product` |

## API Reference

All endpoints are prefixed with `/api/v1` and return the standard envelope:

```json
{ "success": true, "message": "...", "error": null, "data": { ... } }
```

### Categories — `/api/v1/categories`

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all categories |
| `GET` | `/{id}` | Get a category by id |
| `POST` | `/` | Create a category — body: `{ "name": "string" }` |
| `DELETE` | `/{id}` | Delete a category (fails with `409` if products still reference it) |

### Products — `/api/v1/products`

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all products |
| `GET` | `/{id}` | Get a product by id |
| `GET` | `/{id}/details` | Get a product with its category populated |
| `POST` | `/` | Create a product — body: `{ "title", "description", "image", "price", "categoryId", "rating" }` |
| `DELETE` | `/{id}` | Delete a product |
| `GET` | `/search?categoryName={name}` | Find products by category name |
| `GET` | `/categories` | List distinct category names used by products |

### Orders — `/api/v1/orders`

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all orders (with line items) |
| `GET` | `/{id}` | Get an order by id |
| `GET` | `/{id}/summary` | Aggregate summary — distinct item count, total quantity, total amount |
| `POST` | `/` | Create an order — body: `{ "orderItems": [{ "productId", "quantity" }] }` |
| `PUT` | `/{id}` | Update an order's status and/or items — body: `{ "status", "orderItems": [{ "productId", "quantity", "action": "ADD"\|"REMOVE"\|"INCREMENT"\|"DECREMENT" }] }` |
| `DELETE` | `/{id}` | Delete an order |
| `GET` | `/user/{userId}` | *Not implemented* — no `User` entity exists yet |

### Reviews — `/api/v1/reviews`

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all reviews |
| `GET` | `/{id}` | Get a review by id |
| `GET` | `/product/{productId}` | List reviews for a product |
| `GET` | `/order/{orderId}` | List reviews for an order |
| `POST` | `/` | Create a review — body: `{ "comment", "rating", "order": { "id" }, "product": { "id" } }` |
| `DELETE` | `/{id}` | Delete a review |

## Setup

### Prerequisites

- JDK 21
- MySQL 8 running locally (or update the connection settings)
- No need to install Gradle — use the included wrapper (`./gradlew` / `gradlew.bat`)

### 1. Clone and configure the database

Create the database referenced in `src/main/resources/application.yml`:

```sql
CREATE DATABASE fakecommerce;
```

By default the app connects to `jdbc:mysql://localhost:3306/fakecommerce` with user `root` — update `src/main/resources/application.yml` if your credentials differ. Flyway migrations under `src/main/resources/db/migration` run automatically on startup and create all tables.

### 2. (Optional) Configure New Relic

Copy `.env.example` to `.env` and fill in a real license key if you want APM data reported:

```
NEW_RELIC_LICENSE_KEY=your-license-key-here
NEW_RELIC_APP_NAME=FakeCommerce (Dev)
```

The agent jar (`newrelic/newrelic.jar`) is attached automatically via `-javaagent` on `bootRun`. Without a valid license key the app still starts fine — New Relic just won't report data.

### 3. Run the app

```bash
./gradlew bootRun
```

The API is served on `http://localhost:8080`. DevTools will hot-restart the app automatically as you edit source files.

### 4. Run tests

```bash
./gradlew test
```

## Project Structure

```
src/main/java/com/example/FakeCommerce/
├── controllers/   REST endpoints (Category, Product, Order, Review)
├── services/      Business logic, orchestrates repositories + adapters
├── adapters/      Entity → response DTO mapping (stream-based)
├── repositories/  Spring Data JPA repositories
├── schema/        JPA entities (soft-deletable, audited)
├── dtos/          Request/response payloads
├── exceptions/    Domain exceptions + GlobalExceptionHandler
└── utils/         ApiResponse envelope
```
