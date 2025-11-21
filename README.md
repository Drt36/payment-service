# Payment Service

A Spring Boot-based REST API service for managing high-value payment requests with exchange rate configuration, fee calculation, and admin validation workflow.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
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

- Docker & Docker Compose

### Quick Start

1. **Clone the repository**
   ```bash
   git clone git@github.com:Drt36/payment-service.git
   cd payment-service
   ```

2. **Copy environment variables**
   ```bash
   cp .env.example .env
   ```

3. **Copy application configuration**
   ```bash
   cp src/main/resources/application.example.yml src/main/resources/application.yml
   ```

4. **Start the services**
   ```bash
   docker compose up -d
   ```

5. **Check logs**
   ```bash
   docker compose logs -f payment-service
   ```

6. **Verify the application is running**
   - **Health Check**: http://localhost:8080/health
   - **Swagger UI**: http://localhost:8080/swagger-ui/index.html
   - **API Docs**: http://localhost:8080/api-docs

### Useful Docker Compose Commands

```bash
# View logs
docker compose logs -f payment-service

# View MongoDB logs
docker compose logs -f mongodb

# Stop services
docker compose down

# Stop and remove volumes (clears database data)
docker compose down -v

# Rebuild and restart
docker compose up -d --build

# Restart a specific service
docker compose restart payment-service
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
│      Payment Service API        │
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
│            MongoDB Database                 │
│  - payments collection                      │
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

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## API Endpoints

### Health Check

#### GET /health

Check if the service is running.

**Request:**
```bash
curl http://localhost:8080/health
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

---

### Exchange Configuration Endpoints

#### POST /api/v1/exchange-configs

Create a new exchange rate and fee configuration.

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/exchange-configs \
  -H "Content-Type: application/json" \
  -d '{
    "sourceCurrency": "USD",
    "targetCurrency": "EUR",
    "minAmount": 100.00,
    "maxAmount": 100000.00,
    "fxRate": 0.95,
    "feeFlat": 10.00,
    "feePercent": 0.04
  }'
```

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": "67890abcdef1234567890123",
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

#### GET /api/v1/exchange-configs

Get all exchange configurations.

**Request:**
```bash
curl http://localhost:8080/api/v1/exchange-configs
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": "67890abcdef1234567890123",
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
    {
      "id": "67890abcdef1234567890124",
      "sourceCurrency": "GBP",
      "targetCurrency": "USD",
      "minAmount": 50.00,
      "maxAmount": 50000.00,
      "fxRate": 1.25,
      "feeFlat": 5.00,
      "feePercent": 0.03,
      "createdAt": "2025-01-21T11:00:00",
      "updatedAt": "2025-01-21T11:00:00"
    }
  ],
  "timestamp": "2025-01-21T12:00:00"
}
```

#### GET /api/v1/exchange-configs/{id}

Get a specific exchange configuration by ID.

**Request:**
```bash
curl http://localhost:8080/api/v1/exchange-configs/67890abcdef1234567890123
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": "67890abcdef1234567890123",
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

#### PUT /api/v1/exchange-configs/{id}

Update an existing exchange configuration.

**Request:**
```bash
curl -X PUT http://localhost:8080/api/v1/exchange-configs/67890abcdef1234567890123 \
  -H "Content-Type: application/json" \
  -d '{
    "sourceCurrency": "USD",
    "targetCurrency": "EUR",
    "minAmount": 100.00,
    "maxAmount": 100000.00,
    "fxRate": 0.96,
    "feeFlat": 12.00,
    "feePercent": 0.05
  }'
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": "67890abcdef1234567890123",
    "sourceCurrency": "USD",
    "targetCurrency": "EUR",
    "minAmount": 100.00,
    "maxAmount": 100000.00,
    "fxRate": 0.96,
    "feeFlat": 12.00,
    "feePercent": 0.05,
    "createdAt": "2025-01-21T12:00:00",
    "updatedAt": "2025-01-21T12:30:00"
  },
  "timestamp": "2025-01-21T12:30:00"
}
```

#### DELETE /api/v1/exchange-configs/{id}

Delete an exchange configuration (soft delete).

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/v1/exchange-configs/67890abcdef1234567890123
```

**Response (204 No Content):**
```
(No response body)
```

---

### Payment Endpoints

#### POST /api/v1/payments

Create a new payment request. The system will automatically verify, calculate exchange rates, and apply fees.

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -H "X-Admin-Id: admin-123" \
  -d '{
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
  }'
```

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "referenceNumber": "TXN-AbCdEf12GhIj",
    "sender": {
      "name": "John Doe",
      "address": "123 Main St, New York, NY 10001",
      "referenceNumber": "SND-XyZ789AbC",
      "fundingAccount": {
        "accountNumber": "****7890",
        "routingNumber": "****4321",
        "bankCode": "BANK001"
      }
    },
    "receiver": {
      "name": "Jane Smith",
      "address": "456 Oak Ave, Berlin, Germany",
      "referenceNumber": "RCV-AbC123XyZ",
      "account": {
        "accountNumber": "****3210",
        "bankCode": "BANK002",
        "swiftCode": "SWIFT123"
      }
    },
    "sourceCurrency": "USD",
    "targetCurrency": "EUR",
    "sourceCountry": "US",
    "destinationCountry": "DE",
    "sourceAmount": 1000.00,
    "targetAmount": 940.00,
    "purpose": "Payment for services",
    "corridor": "US-EU",
    "status": "PENDING_ADMIN_REVIEW",
    "exchangeRateCalculation": {
      "exchangeConfigId": "67890abcdef1234567890123",
      "exchangeRate": 0.95,
      "sourceCurrency": "USD",
      "targetCurrency": "EUR",
      "appliedAt": "2025-01-21T12:00:00"
    },
    "feeCalculation": {
      "feeFlat": 10.00,
      "feePercent": 0.04,
      "flatFeeAmount": 10.00,
      "percentFeeAmount": 40.00,
      "totalFee": 50.00,
      "calculatedAt": "2025-01-21T12:00:00"
    },
    "createdBy": "admin-123",
    "validatedBy": null,
    "systemVerified": false,
    "createdAt": "2025-01-21T12:00:00",
    "validatedAt": null,
    "estimatedDeliveryDate": "2025-01-23T12:00:00",
    "updatedAt": "2025-01-21T12:00:00"
  },
  "timestamp": "2025-01-21T12:00:00"
}
```

#### GET /api/v1/payments

Get all payments with optional filters (status, date range, sender reference) and pagination.

**Request:**
```bash
# Get all payments
curl "http://localhost:8080/api/v1/payments?page=0&size=20"

# Filter by status
curl "http://localhost:8080/api/v1/payments?status=PENDING_ADMIN_REVIEW&page=0&size=20"

# Filter by date range
curl "http://localhost:8080/api/v1/payments?dateFrom=2025-01-01T00:00:00&dateTo=2025-01-31T23:59:59&page=0&size=20"

# Filter by sender reference
curl "http://localhost:8080/api/v1/payments?senderReference=SND-XyZ789AbC&page=0&size=20"

# Combined filters
curl "http://localhost:8080/api/v1/payments?status=PENDING_ADMIN_REVIEW&dateFrom=2025-01-01T00:00:00&dateTo=2025-01-31T23:59:59&senderReference=SND-XyZ789AbC&page=0&size=20"
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439011",
        "referenceNumber": "TXN-AbCdEf12GhIj",
        "sender": {
          "name": "John Doe",
          "address": "123 Main St, New York, NY 10001",
          "referenceNumber": "SND-XyZ789AbC",
          "fundingAccount": {
            "accountNumber": "****7890",
            "routingNumber": "****4321",
            "bankCode": "BANK001"
          }
        },
        "receiver": {
          "name": "Jane Smith",
          "address": "456 Oak Ave, Berlin, Germany",
          "referenceNumber": "RCV-AbC123XyZ",
          "account": {
            "accountNumber": "****3210",
            "bankCode": "BANK002",
            "swiftCode": "SWIFT123"
          }
        },
        "sourceCurrency": "USD",
        "targetCurrency": "EUR",
        "sourceCountry": "US",
        "destinationCountry": "DE",
        "sourceAmount": 1000.00,
        "targetAmount": 940.00,
        "purpose": "Payment for services",
        "corridor": "US-EU",
        "status": "PENDING_ADMIN_REVIEW",
        "exchangeRateCalculation": {
          "exchangeConfigId": "67890abcdef1234567890123",
          "exchangeRate": 0.95,
          "sourceCurrency": "USD",
          "targetCurrency": "EUR",
          "appliedAt": "2025-01-21T12:00:00"
        },
        "feeCalculation": {
          "feeFlat": 10.00,
          "feePercent": 0.04,
          "flatFeeAmount": 10.00,
          "percentFeeAmount": 40.00,
          "totalFee": 50.00,
          "calculatedAt": "2025-01-21T12:00:00"
        },
        "createdBy": "admin-123",
        "validatedBy": null,
        "systemVerified": false,
        "createdAt": "2025-01-21T12:00:00",
        "validatedAt": null,
        "estimatedDeliveryDate": "2025-01-23T12:00:00",
        "updatedAt": "2025-01-21T12:00:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "size": 20,
    "number": 0,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  },
  "timestamp": "2025-01-21T12:00:00"
}
```

#### GET /api/v1/payments/{id}

Get a specific payment by ID with full details including status history.

**Request:**
```bash
curl http://localhost:8080/api/v1/payments/507f1f77bcf86cd799439011
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "referenceNumber": "TXN-AbCdEf12GhIj",
    "sender": {
      "name": "John Doe",
      "address": "123 Main St, New York, NY 10001",
      "referenceNumber": "SND-XyZ789AbC",
      "fundingAccount": {
        "accountNumber": "****7890",
        "routingNumber": "****4321",
        "bankCode": "BANK001"
      }
    },
    "receiver": {
      "name": "Jane Smith",
      "address": "456 Oak Ave, Berlin, Germany",
      "referenceNumber": "RCV-AbC123XyZ",
      "account": {
        "accountNumber": "****3210",
        "bankCode": "BANK002",
        "swiftCode": "SWIFT123"
      }
    },
    "sourceCurrency": "USD",
    "targetCurrency": "EUR",
    "sourceCountry": "US",
    "destinationCountry": "DE",
    "sourceAmount": 1000.00,
    "targetAmount": 940.00,
    "purpose": "Payment for services",
    "corridor": "US-EU",
    "status": "APPROVED",
    "exchangeRateCalculation": {
      "exchangeConfigId": "67890abcdef1234567890123",
      "exchangeRate": 0.95,
      "sourceCurrency": "USD",
      "targetCurrency": "EUR",
      "appliedAt": "2025-01-21T12:00:00"
    },
    "feeCalculation": {
      "feeFlat": 10.00,
      "feePercent": 0.04,
      "flatFeeAmount": 10.00,
      "percentFeeAmount": 40.00,
      "totalFee": 50.00,
      "calculatedAt": "2025-01-21T12:00:00"
    },
    "createdBy": "admin-123",
    "validatedBy": "admin-123",
    "systemVerified": true,
    "createdAt": "2025-01-21T12:00:00",
    "validatedAt": "2025-01-21T12:05:00",
    "estimatedDeliveryDate": "2025-01-23T12:00:00",
    "updatedAt": "2025-01-21T12:05:00",
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

#### PATCH /api/v1/payments/{id}/validate

Update payment status (approve, reject, etc.). Requires system verification to be completed first.

**Request:**
```bash
# Approve payment
curl -X PATCH http://localhost:8080/api/v1/payments/507f1f77bcf86cd799439011/validate \
  -H "Content-Type: application/json" \
  -H "X-Admin-Id: admin-123" \
  -d '{
    "status": "APPROVED",
    "note": "Reviewed and validated by admin"
  }'

# Reject payment
curl -X PATCH http://localhost:8080/api/v1/payments/507f1f77bcf86cd799439011/validate \
  -H "Content-Type: application/json" \
  -H "X-Admin-Id: admin-123" \
  -d '{
    "status": "REJECTED",
    "note": "Insufficient documentation"
  }'

# Mark as delivered
curl -X PATCH http://localhost:8080/api/v1/payments/507f1f77bcf86cd799439011/validate \
  -H "Content-Type: application/json" \
  -H "X-Admin-Id: admin-123" \
  -d '{
    "status": "DELIVERED",
    "note": "Payment successfully delivered"
  }'
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "referenceNumber": "TXN-AbCdEf12GhIj",
    "sender": {
      "name": "John Doe",
      "address": "123 Main St, New York, NY 10001",
      "referenceNumber": "SND-XyZ789AbC",
      "fundingAccount": {
        "accountNumber": "****7890",
        "routingNumber": "****4321",
        "bankCode": "BANK001"
      }
    },
    "receiver": {
      "name": "Jane Smith",
      "address": "456 Oak Ave, Berlin, Germany",
      "referenceNumber": "RCV-AbC123XyZ",
      "account": {
        "accountNumber": "****3210",
        "bankCode": "BANK002",
        "swiftCode": "SWIFT123"
      }
    },
    "sourceCurrency": "USD",
    "targetCurrency": "EUR",
    "sourceCountry": "US",
    "destinationCountry": "DE",
    "sourceAmount": 1000.00,
    "targetAmount": 940.00,
    "purpose": "Payment for services",
    "corridor": "US-EU",
    "status": "APPROVED",
    "exchangeRateCalculation": {
      "exchangeConfigId": "67890abcdef1234567890123",
      "exchangeRate": 0.95,
      "sourceCurrency": "USD",
      "targetCurrency": "EUR",
      "appliedAt": "2025-01-21T12:00:00"
    },
    "feeCalculation": {
      "feeFlat": 10.00,
      "feePercent": 0.04,
      "flatFeeAmount": 10.00,
      "percentFeeAmount": 40.00,
      "totalFee": 50.00,
      "calculatedAt": "2025-01-21T12:00:00"
    },
    "createdBy": "admin-123",
    "validatedBy": "admin-123",
    "systemVerified": true,
    "createdAt": "2025-01-21T12:00:00",
    "validatedAt": "2025-01-21T12:05:00",
    "estimatedDeliveryDate": "2025-01-23T12:00:00",
    "updatedAt": "2025-01-21T12:05:00"
  },
  "timestamp": "2025-01-21T12:05:00"
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
- Sensitive are stored in encrypted form
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

