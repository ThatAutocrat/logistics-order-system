# 📦 Logistics Order Management System

A production-ready Spring Boot backend service for managing logistics orders with strict lifecycle enforcement, duplicate prevention, and full audit history.

---

## 🏗️ Architecture

```
src/main/java/com/logistics/order/
├── controller/         # REST endpoints (OrderController)
├── service/            # Business logic (OrderService + Impl)
├── repository/         # Data access (Spring Data JPA)
├── entity/             # JPA entities (Order, StatusHistory)
├── dto/
│   ├── request/        # CreateOrderRequest, UpdateStatusRequest
│   └── response/       # OrderResponse, StatusHistoryResponse, ApiResponse
├── enums/              # OrderStatus (with lifecycle rules)
├── exception/          # Custom exceptions + GlobalExceptionHandler
├── mapper/             # Entity ↔ DTO mapping (OrderMapper)
└── config/             # SwaggerConfig
```

**Layered architecture:** Controller → Service → Repository → Entity

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.4 |
| Language | Java 17 |
| Database | H2 (in-memory, auto-configured) |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |
| Containerization | Docker + Docker Compose |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run Locally

```bash
# Clone the repository
git clone https://github.com/your-username/logistics-order-system.git
cd logistics-order-system

# Build and run
mvn spring-boot:run
```

The application starts at **http://localhost:8080**

### Run with Docker

```bash
# Build image and start container
docker-compose up --build

# Stop
docker-compose down
```

### Run Tests

```bash
mvn test
```

---

## 📖 API Documentation

Interactive Swagger UI is available at:
**http://localhost:8080/swagger-ui.html**

Raw OpenAPI JSON: **http://localhost:8080/api-docs**

H2 Console: **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:logistics_db`)

---

## 📋 API Reference

### Base URL
```
http://localhost:8080/api/v1
```

### Standard Response Envelope

All endpoints return a consistent wrapper:
```json
{
  "success": true,
  "message": "Order created successfully.",
  "data": { ... },
  "timestamp": "2024-04-28T10:30:00"
}
```

---

### 1. Create Order

**`POST /api/v1/orders`**

Creates a new logistics order.

**Request Body:**
```json
{
  "customerName": "Alice Smith",
  "customerEmail": "alice@example.com",
  "customerPhone": "+91-9876543210",
  "pickupAddress": "123 Warehouse Road, Mumbai, Maharashtra 400001",
  "deliveryAddress": "456 Customer Lane, Delhi 110001",
  "priority": "HIGH",
  "weightKg": 5.5,
  "notes": "Handle with care – fragile items"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `customerName` | String | ✅ | 2–100 characters |
| `customerEmail` | String | ✅ | Valid email format |
| `customerPhone` | String | ❌ | Valid phone format |
| `pickupAddress` | String | ✅ | 5–300 characters |
| `deliveryAddress` | String | ✅ | 5–300 characters |
| `priority` | String | ❌ | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `weightKg` | Double | ❌ | 0.01–10,000 |
| `notes` | String | ❌ | Max 1000 chars |

**Responses:**
- `201 Created` – Order created
- `400 Bad Request` – Validation errors
- `409 Conflict` – Duplicate order exists

---

### 2. Get All Orders

**`GET /api/v1/orders`**

Returns all orders. Supports optional status filter.

**Query Parameters:**

| Param | Type | Required | Example |
|---|---|---|---|
| `status` | OrderStatus | ❌ | `CREATED`, `PICKED_UP`, `IN_TRANSIT`, `DELIVERED` |

**Examples:**
```
GET /api/v1/orders
GET /api/v1/orders?status=IN_TRANSIT
```

**Responses:**
- `200 OK` – List of orders (empty array if none)

---

### 3. Get Order by ID

**`GET /api/v1/orders/{id}`**

Returns full order details including complete status history.

**Responses:**
- `200 OK` – Order details
- `404 Not Found` – Order not found

---

### 4. Update Order Status

**`PATCH /api/v1/orders/{id}/status`**

Advances the order through the status lifecycle.

**Valid Lifecycle:**
```
CREATED → PICKED_UP → IN_TRANSIT → DELIVERED
```

**Request Body:**
```json
{
  "newStatus": "PICKED_UP",
  "remarks": "Package collected from sender at 10:30 AM"
}
```

**Responses:**
- `200 OK` – Status updated
- `404 Not Found` – Order not found
- `422 Unprocessable Entity` – Invalid transition or already delivered

---

## 🔄 Order Status Lifecycle

```
┌─────────┐     ┌───────────┐     ┌────────────┐     ┌───────────┐
│ CREATED │────▶│ PICKED_UP │────▶│ IN_TRANSIT │────▶│ DELIVERED │
└─────────┘     └───────────┘     └────────────┘     └───────────┘
```

**Rules enforced:**
- ✅ Only forward transitions allowed
- ❌ No skipping steps (e.g., CREATED → IN_TRANSIT is rejected)
- ❌ No backward transitions (e.g., IN_TRANSIT → CREATED is rejected)
- ❌ DELIVERED is terminal – no further updates allowed

---

## 🛡️ Business Rules

1. **Duplicate Prevention** – An order is considered duplicate if the same `customerEmail` + `pickupAddress` + `deliveryAddress` combination already exists.
2. **Status Lifecycle** – Strict one-direction state machine, enforced in `OrderStatus` enum.
3. **Audit History** – Every status change (including initial creation) is recorded with a timestamp and optional remarks.

---

## 🗂️ Example cURL Requests

```bash
# Create an order
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Smith",
    "customerEmail": "alice@example.com",
    "pickupAddress": "123 Pickup St, Mumbai",
    "deliveryAddress": "456 Delivery Ave, Delhi",
    "priority": "HIGH",
    "weightKg": 2.5
  }'

# Get all orders
curl http://localhost:8080/api/v1/orders

# Get orders by status
curl "http://localhost:8080/api/v1/orders?status=CREATED"

# Get order by ID
curl http://localhost:8080/api/v1/orders/{id}

# Advance status to PICKED_UP
curl -X PATCH http://localhost:8080/api/v1/orders/{id}/status \
  -H "Content-Type: application/json" \
  -d '{"newStatus": "PICKED_UP", "remarks": "Collected from sender"}'
```

---

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report
```

Tests cover:
- Order creation (success + duplicate)
- Order retrieval (found + not found)
- Status lifecycle (valid + all invalid transitions)
- Terminal state protection (DELIVERED)

---

## 📁 Project Structure

```
logistics-order-system/
├── src/
│   ├── main/
│   │   ├── java/com/logistics/order/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/logistics/order/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```
