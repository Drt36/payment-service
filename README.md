# Payment Service

A Spring Boot-based REST API service for managing high-value payment requests with exchange rate configuration, fee calculation, and admin validation workflow.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [How to Run](#how-to-run)
- [Architecture](#architecture)
- [API Documentation](#api-documentation)
- [Sample Requests/Responses](#sample-requestsresponses)
- [Running Tests](#running-tests)
- [Business Logic](#business-logic)
- [Future Enhancements](#future-enhancements)

## Features

- ✅ Payment request creation with automatic fee and exchange rate calculation
- ✅ Admin validation workflow for payment approval/rejection
- ✅ Exchange rate and fee configuration management
- ✅ Idempotency support to prevent duplicate payments
- ✅ Asynchronous system verification
- ✅ Sensitive data encryption (account numbers, routing numbers)
- ✅ Soft delete for exchange configurations
- ✅ Comprehensive API documentation with Swagger/OpenAPI
- ✅ Health check endpoint
- ✅ CORS support

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.5.7** - Application framework
- **MongoDB** - Database
- **MapStruct 1.6.3** - Object mapping
- **Lombok** - Boilerplate code reduction
- **SpringDoc OpenAPI 2.7.0** - API documentation
- **Maven** - Build tool
- **Docker & Docker Compose** - Containerization

## Project Structure

```
payment-service/
├── src/
│   ├── main/
│   │   ├── java/com/xuno/payment/
│   │   │   ├── PaymentServiceApplication.java       # Main application class
│   │   │   ├── common/                              # Shared components
│   │   │   │   ├── annotation/                      # Custom annotations
│   │   │   │   │   └── ApiDefaultErrors.java
│   │   │   │   ├── dto/                            # Common DTOs
│   │   │   │   │   └── GlobalApiResponse.java
│   │   │   │   ├── exception/                      # Exception handling
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   └── ResourceNotFoundException.java
│   │   │   │   └── validation/                     # Custom validators
│   │   │   │       ├── AccountNumber.java
│   │   │   │       ├── RoutingNumber.java
│   │   │   │       └── Sanitized.java
│   │   │   ├── config/                             # Configuration
│   │   │   │   ├── OpenApiConfig.java              # Swagger configuration
│   │   │   │   └── WebMvcConfig.java               # CORS configuration
│   │   │   ├── health/                             # Health check
│   │   │   │   └── HealthController.java
│   │   │   ├── exchangeconfig/                     # Exchange Config Module
│   │   │   │   ├── controller/
│   │   │   │   │   └── ExchangeConfigController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── ExchangeConfigService.java
│   │   │   │   │   └── impl/
│   │   │   │   │       └── ExchangeConfigServiceImpl.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── ExchangeConfigRepository.java
│   │   │   │   ├── mapper/
│   │   │   │   │   └── ExchangeConfigMapper.java
│   │   │   │   └── model/
│   │   │   │       ├── entity/
│   │   │   │       │   └── ExchangeRateConfiguration.java
│   │   │   │       └── dto/
│   │   │   │           ├── ExchangeConfigRequest.java
│   │   │   │           └── ExchangeConfigResponse.java
│   │   │   └── payment/                            # Payment Module
│   │   │       ├── controller/
│   │   │       │   └── PaymentController.java
│   │   │       ├── service/
│   │   │       │   ├── PaymentService.java
│   │   │       │   ├── EncryptionService.java
│   │   │       │   ├── ExchangeRateService.java
│   │   │       │   ├── FeeCalculationService.java
│   │   │       │   ├── SystemVerificationService.java
│   │   │       │   └── impl/
│   │   │       │       ├── PaymentServiceImpl.java
│   │   │       │       ├── EncryptionServiceImpl.java
│   │   │       │       ├── ExchangeRateServiceImpl.java
│   │   │       │       ├── FeeCalculationServiceImpl.java
│   │   │       │       └── SystemVerificationServiceImpl.java
│   │   │       ├── repository/
│   │   │       │   ├── PaymentRepository.java
│   │   │       │   └── PaymentSpecification.java
│   │   │       ├── mapper/
│   │   │       │   └── PaymentMapper.java
│   │   │       ├── util/
│   │   │       │   └── ReferenceNumberGenerator.java
│   │   │       └── model/
│   │   │           ├── entity/
│   │   │           │   └── Payment.java
│   │   │           ├── enums/
│   │   │           │   ├── PaymentStatus.java
│   │   │           │   └── UserRole.java
│   │   │           ├── valueobject/
│   │   │           │   ├── SenderInfo.java
│   │   │           │   ├── SenderFundingAccountInfo.java
│   │   │           │   ├── ReceiverInfo.java
│   │   │           │   ├── ReceiverAccountInfo.java
│   │   │           │   ├── ExchangeRateCalculationResult.java
│   │   │           │   ├── FeeCalculationResult.java
│   │   │           │   └── StatusHistory.java
│   │   │           └── dto/
│   │   │               ├── PaymentRequest.java
│   │   │               ├── PaymentResponse.java
│   │   │               ├── PaymentDetailResponse.java
│   │   │               └── StatusUpdateRequest.java
│   │   └── resources/
│   │       ├── application.yml                      # Application configuration
│   │       └── application.example.yml            # Configuration template
│   └── test/                                       # Test files
│       └── java/com/xuno/payment/
│           ├── payment/
│           │   └── service/impl/
│           │       ├── PaymentServiceImplTest.java
│           │       └── EncryptionServiceImplTest.java
│           └── exchangeconfig/
│               └── service/impl/
│                   └── ExchangeConfigServiceImplTest.java
├── Dockerfile                                      # Docker image definition
├── docker-compose.yml                              # Docker Compose configuration
├── .dockerignore                                  # Docker ignore patterns
├── .env.example                                   # Environment variables template
├── .gitignore                                     # Git ignore patterns
└── pom.xml                                        # Maven configuration
```

## Setup Instructions

### Prerequisites

- Java 21 or higher
- Maven 3.9 or higher
- MongoDB (local or Atlas)
- Docker & Docker Compose (optional, for containerized setup)

### Local Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd payment-service
   ```

2. **Configure application properties**
   ```bash
   # Copy the example configuration
   cp src/main/resources/application.example.yml src/main/resources/application.yml
   
   # Edit application.yml with your MongoDB connection string and encryption secret
   # Generate encryption secret: openssl rand -base64 32
   ```

3. **Configure environment variables (optional)**
   ```bash
   # Copy the example environment file
   cp .env.example .env
   
   # Edit .env with your actual values
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The service will start on `http://localhost:8080`

## How to Run

### Using Maven

```bash
# Run the application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Using Docker Compose

```bash
# Build and start all services (app + MongoDB)
docker-compose up -d

# View logs
docker-compose logs -f payment-service

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Using Docker (standalone)

```bash
# Build the image
docker build -t payment-service:latest .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/payment_db \
  -e APP_ENCRYPTION_SECRET=your-secret-key \
  payment-service:latest
```

## Architecture

### High-Level Architecture

```
┌─────────────────┐
│   Client App    │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────┐
│      Payment Service API         │
│  (Spring Boot REST Controller)  │
└────────┬────────────────────────┘
         │
         ├─────────────────┬─────────────────┐
         ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   Payment    │  │   Exchange   │  │  Encryption  │
│   Service    │  │   Config     │  │   Service    │
│              │  │   Service    │  │              │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                 │                 │
       ▼                 ▼                 ▼
┌─────────────────────────────────────────────┐
│            MongoDB Database                  │
│  - payments collection                       │
│  - exchange_configs collection              │
└─────────────────────────────────────────────┘
```

### Module Architecture

#### Payment Module
- **Controller**: Handles HTTP requests/responses
- **Service**: Business logic (creation, validation, retrieval)
- **Repository**: Data access layer
- **Mapper**: DTO ↔ Entity conversion
- **Model**: Entities, DTOs, Value Objects, Enums

#### Exchange Config Module
- **Controller**: CRUD operations for exchange configurations
- **Service**: Business logic for exchange rate management
- **Repository**: MongoDB queries with soft delete support
- **Mapper**: DTO ↔ Entity conversion

### Design Patterns

- **Service Layer Pattern**: Separation of business logic from controllers
- **Repository Pattern**: Abstraction of data access
- **DTO Pattern**: Data transfer objects for API communication
- **Value Object Pattern**: Immutable domain objects (StatusHistory, ExchangeRateCalculationResult)
- **Specification Pattern**: Dynamic query building for payment filtering
- **Strategy Pattern**: Different verification strategies (SystemVerificationService)

## API Documentation

Once the application is running, access the API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Sample Requests/Responses

### 1. Health Check

**Request:**
```http
GET /health
```

**Response:**
```json
{
  "success": true,
  "data": {
    "status": "UP",
    "message": "Server is running"
  },
  "timestamp": "2025-01-21T12:00:00"
}
```

### 2. Create Exchange Configuration

**Request:**
```http
POST /api/v1/exchange-configs
Content-Type: application/json

{
  "sourceCurrency": "USD",
  "targetCurrency": "EUR",
  "minAmount": 100.00,
  "maxAmount": 100000.00,
  "fxRate": 0.95,
  "feeFlat": 10.00,
  "feePercent": 0.04
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "config-123",
    "sourceCurrency": "USD",
    "targetCurrency": "EUR",
    "minAmount": 100.00,
    "maxAmount": 100000.00,
    "fxRate": 0.95,
    "feeFlat": 10.00,
    "feePercent": 0.04,
    "createdAt": "2025-01-21T12:00:00",
    "updatedAt": "2025-01-21T12:00:00"
  },
  "timestamp": "2025-01-21T12:00:00"
}
```

### 3. Create Payment

**Request:**
```http
POST /api/v1/payments
Content-Type: application/json
X-Admin-Id: admin-123

{
  "idempotencyKey": "unique-payment-key-12345",
  "sender": {
    "name": "John Doe",
    "address": "123 Main St, New York, NY 10001",
    "fundingAccount": {
      "accountNumber": "1234567890",
      "bankCode": "BANK001",
      "routingNumber": "987654321"
    }
  },
  "receiver": {
    "name": "Jane Smith",
    "address": "456 Oak Ave, Berlin, Germany",
    "account": {
      "accountNumber": "9876543210",
      "bankCode": "BANK002",
      "swiftCode": "SWIFT123"
    }
  },
  "sourceCurrency": "USD",
  "targetCurrency": "EUR",
  "sourceCountry": "US",
  "destinationCountry": "DE",
  "sourceAmount": 1000.00,
  "purpose": "Payment for services",
  "corridor": "US-EU"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "payment-123",
    "referenceNumber": "TXN-AbCdEf12",
    "sender": {
      "name": "John Doe",
      "address": "123 Main St, New York, NY 10001",
      "referenceNumber": "SND-XyZ789",
      "fundingAccount": {
        "accountNumber": "****7890",
        "routingNumber": "****4321",
        "bankCode": "BANK001"
      }
    },
    "receiver": {
      "name": "Jane Smith",
      "address": "456 Oak Ave, Berlin, Germany",
      "referenceNumber": "RCV-AbC123",
      "account": {
        "accountNumber": "****3210",
        "bankCode": "BANK002",
        "swiftCode": "SWIFT123"
      }
    },
    "sourceCurrency": "USD",
    "targetCurrency": "EUR",
    "sourceAmount": 1000.00,
    "targetAmount": 900.00,
    "status": "PENDING_ADMIN_REVIEW",
    "systemVerified": false,
    "createdAt": "2025-01-21T12:00:00"
  },
  "timestamp": "2025-01-21T12:00:00"
}
```

### 4. Validate Payment (Approve)

**Request:**
```http
PATCH /api/v1/payments/{id}/validate
Content-Type: application/json
X-Admin-Id: admin-123

{
  "status": "APPROVED",
  "note": "Reviewed and validated by admin"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "payment-123",
    "status": "APPROVED",
    "validatedBy": "admin-123",
    "updatedAt": "2025-01-21T12:05:00"
  },
  "timestamp": "2025-01-21T12:05:00"
}
```

### 5. Get Payment by ID (with Status History)

**Request:**
```http
GET /api/v1/payments/{id}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "payment-123",
    "referenceNumber": "TXN-AbCdEf12",
    "status": "APPROVED",
    "statusHistory": [
      {
        "status": "PENDING_ADMIN_REVIEW",
        "changedBy": "admin-123",
        "changedByRole": "ADMIN",
        "changedAt": "2025-01-21T12:00:00",
        "note": "Payment created"
      },
      {
        "status": "APPROVED",
        "changedBy": "admin-123",
        "changedByRole": "ADMIN",
        "changedAt": "2025-01-21T12:05:00",
        "note": "Reviewed and validated by admin"
      }
    ]
  },
  "timestamp": "2025-01-21T12:05:00"
}
```

### 6. Get All Payments (with filters)

**Request:**
```http
GET /api/v1/payments?status=PENDING_ADMIN_REVIEW&page=0&size=20
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "payment-123",
        "referenceNumber": "TXN-AbCdEf12",
        "status": "PENDING_ADMIN_REVIEW",
        "sourceAmount": 1000.00,
        "targetAmount": 900.00
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 20,
    "number": 0
  },
  "timestamp": "2025-01-21T12:00:00"
}
```

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
# Payment service tests
mvn test -Dtest=PaymentServiceImplTest

# Exchange config service tests
mvn test -Dtest=ExchangeConfigServiceImplTest

# Encryption service tests
mvn test -Dtest=EncryptionServiceImplTest
```

### Run Tests with Coverage

```bash
mvn test jacoco:report
```

### Test Coverage

The project includes comprehensive unit tests covering:
- Payment creation, validation, and retrieval
- Exchange configuration CRUD operations
- Encryption/decryption functionality
- Business logic validation
- Error handling scenarios

## Business Logic

### Payment Creation Flow

1. **Idempotency Check**: Validates that the idempotency key is unique
2. **Initial Verification**: System performs basic validation (amount > 0, currencies different, etc.)
3. **Exchange Rate Lookup**: Finds matching exchange configuration based on currency pair and amount range
4. **Exchange Rate Calculation**: Calculates the exchange rate using the configuration
5. **Fee Calculation**: Calculates flat fee and percentage fee
6. **Target Amount Calculation**: `(sourceAmount × fxRate) - totalFees`
7. **Reference Number Generation**: Generates unique reference numbers for transaction, sender, and receiver
8. **Encryption**: Encrypts sensitive account and routing numbers
9. **Status History**: Records initial status as `PENDING_ADMIN_REVIEW`
10. **Save**: Persists payment to database
11. **Async Verification**: Triggers background system verification

### Payment Validation Flow

1. **System Verification Check**: Ensures payment has been verified by system
2. **Status Transition Validation**: Validates that the status transition is allowed
3. **Status Update**: Updates payment status
4. **Status History**: Records the status change with admin details and note
5. **Save**: Persists updated payment

### Status Transition Rules

- `PENDING_ADMIN_REVIEW` → `APPROVED` ✅
- `PENDING_ADMIN_REVIEW` → `REJECTED` ✅
- `PENDING_ADMIN_REVIEW` → `MISINFORMATION_SENDER` ✅
- `PENDING_ADMIN_REVIEW` → `MISINFORMATION_RECEIVER` ✅
- `APPROVED` → `DELIVERED` ✅
- `APPROVED` → `REJECTED` ❌ (Not allowed)
- `DELIVERED` → Any status ❌ (Not allowed)

### Exchange Configuration Matching

- Matches by `sourceCurrency` + `targetCurrency`
- Amount must be within `minAmount ≤ amount ≤ maxAmount`
- Returns the latest configuration (sorted by `createdAt` descending) if multiple matches
- Throws exception if no match found

### Fee Calculation

- **Flat Fee**: Applied as-is from configuration
- **Percentage Fee**: `sourceAmount × (feePercent / 100)`
- **Total Fee**: `flatFee + percentFee`
- **Target Amount**: `(sourceAmount × fxRate) - totalFee`

### Data Encryption

- Account numbers and routing numbers are encrypted using AES-256
- Only last 4 digits are stored (encrypted)
- When retrieved, data is decrypted and masked as `****1234`
- Encryption key is configured via `app.encryption.secret`

## Future Enhancements

### Security & Authentication

- **JWT Authentication**: Implement JWT-based authentication for API endpoints
  - Token generation and validation
  - Role-based access control (RBAC)
  - Token refresh mechanism
  - Integration with Spring Security

- **OAuth2 Integration**: Support for OAuth2 providers (Google, Microsoft, etc.)

- **API Rate Limiting**: Prevent abuse with rate limiting
  - Per-user rate limits
  - Per-endpoint rate limits
  - Redis-based rate limiting

### Performance & Scalability

- **Redis Caching**: 
  - Cache exchange rate configurations
  - Cache frequently accessed payments
  - Session management
  - Distributed locking for concurrent operations

- **Database Indexing**: 
  - Additional indexes for common query patterns
  - Composite indexes optimization

- **Connection Pooling**: 
  - Optimize MongoDB connection pool
  - Database connection monitoring

### Observability & Monitoring

- **Log Rotation**: 
  - Configure logback with file rotation
  - Log aggregation (ELK stack, Splunk)
  - Structured logging (JSON format)

- **Metrics & Monitoring**:
  - Micrometer integration
  - Prometheus metrics endpoint
  - Grafana dashboards
  - Application Performance Monitoring (APM)

- **Distributed Tracing**:
  - OpenTelemetry integration
  - Request tracing across services

### Reliability & Resilience

- **Retry Logic**: 
  - Automatic retry for transient failures
  - Exponential backoff

- **Message Queue**: 
  - RabbitMQ/Kafka for async processing
  - Event-driven architecture
  - Payment status notifications

### Data Management

- **Audit Logging**: 
  - Comprehensive audit trail
  - Who did what and when

- **Data Archival**: 
  - Archive old payments
  - Cold storage strategy

- **Backup & Recovery**: 
  - Automated database backups
  - Point-in-time recovery

### Additional Features

- **Webhook Support**: 
  - Payment status change notifications
  - Configurable webhook endpoints

- **Multi-currency Support**: 
  - Support for more currency pairs
  - Real-time exchange rate updates

- **Payment Scheduling**: 
  - Scheduled/recurring payments
  - Payment reminders

- **Reporting & Analytics**: 
  - Payment analytics dashboard
  - Export functionality (CSV, PDF)
  - Business intelligence integration

- **Internationalization**: 
  - Multi-language support
  - Localized error messages

- **API Versioning**: 
  - Support for multiple API versions
  - Backward compatibility

## Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_DATA_MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/payment_db` |
| `SPRING_DATA_MONGODB_DATABASE` | Database name | `payment_db` |
| `APP_ENCRYPTION_SECRET` | Base64-encoded 32-byte encryption key | Generate with: `openssl rand -base64 32` |

