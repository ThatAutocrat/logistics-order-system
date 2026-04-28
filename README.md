# 📦 Logistics Order Management System

A production-ready REST API built with Spring Boot for managing logistics orders with strict lifecycle enforcement, duplicate prevention, and full audit history.

🔗 **Live API:** https://your-render-url.onrender.com/swagger-ui.html

---

## 🛠️ Tech Stack

- **Java 17** + **Spring Boot 3.2.4**
- **Spring Data JPA** / Hibernate
- **H2** in-memory database
- **SpringDoc OpenAPI** (Swagger UI)
- **Maven**
- **Docker**

---

## ✨ Features

- Create orders with customer, pickup, and delivery details
- Strict status lifecycle: `CREATED → PICKED_UP → IN_TRANSIT → DELIVERED`
- No skipping steps, no backward transitions
- Duplicate order prevention
- Full status change history with timestamps
- Structured API responses with proper HTTP status codes
- Input validation and clean exception handling
- Swagger UI for interactive API documentation

---

## 🚀 Run Locally

### Prerequisites
- Java 17+
- Maven 3.8+

```bash
git clone https://github.com/ThatAutocrat/logistics-order-system.git
cd logistics-order-system
mvn spring-boot:run
```



### Run with Docker
```bash
docker-compose up --build
```

---

### More information located in readme file inside the folder.
