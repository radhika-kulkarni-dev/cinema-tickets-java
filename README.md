# 🎟️ Ticket Service – Clean Architecture Java Implementation

A lightweight, production‑grade Java application implementing a ticket‑purchase workflow with full validation, pricing, seat calculation, configuration loading, and integration with external payment and seat‑reservation services.

This project demonstrates:

- Clean, intention‑revealing design
- SOLID principles
- Stateless domain services
- Centralised configuration
- Comprehensive validation
- Full unit test coverage
- Logging for observability
- No frameworks (pure Java + Maven)

---

## 📁 Project Structure

```
src/
└── main/
    ├── java/
    │   └── uk.gov.dwp.uc.pairtest/
    │       ├── TicketService.java
    │       ├── TicketServiceImpl.java
    │       ├── calculators/
    │       │   ├── TicketPriceCalculator.java
    │       │   └── SeatReservationCalculator.java
    │       ├── validators/
    │       │   └── TicketPurchaseValidator.java
    │       ├── configs/
    │       │   └── ConfigurationProvider.java
    │       └── domain/
    │           └── TicketTypeRequest.java
    └── resources/
        └── application.properties 

```
---

## 🚀 Overview

The **Ticket Service** processes ticket purchase requests by:

1. Validating all business rules
2. Calculating total ticket price
3. Calculating total seats (infants excluded)
4. Calling external services to:
    - Charge the customer
    - Reserve seats

The system is designed to be:

- **Extensible**
- **Testable**
- **Maintainable**
- **Observable**

---

## 🧠 Core Components

### 1. `TicketPurchaseValidator`
Encapsulates **all business rules**:

- Account ID must be > 0
- At least one request must be provided
- No null entries
- No zero or negative ticket counts
- Total tickets must not exceed the configured max number of tickets
- At least one adult must be present in every purchase request

This class is **pure validation logic**.

---

### 2. `TicketPriceCalculator`
A **singleton** stateless domain service that calculates total price using:

- `ADULT_PRICE`
- `CHILD_PRICE`
- `INFANT_PRICE`

Loaded from `ConfigurationProvider`.

---

### 3. `SeatReservationCalculator`
A **singleton** stateless domain service that calculates:
```
total_seats = adult tickets + child tickets
```

Infants do **not** get seats.

---

### 4. `ConfigurationProvider`
A static, eager‑loaded configuration reader that loads:

- `ticket.max.count`
- `ticket.price.adult`
- `ticket.price.child`
- `ticket.price.infant`

from `application.properties`.

It fails fast on:

- Missing properties
- Invalid integers
- Negative or zero values where not allowed

---

### 5. `TicketServiceImpl`
The orchestrator:

1. Validates input
2. Calculates price
3. Calculates seats
4. Calls external services for booking seats and making payment
5. Logs each step

Dependencies are injected for testability.

---

## 🧪 Unit Tests

The project includes **full test coverage** for:

### ✔ `TicketServiceImplTest`
- Successful purchase
- Validation failure
- Zero amount
- Zero seats
- Boundary conditions
- Interaction verification

### ✔ `TicketPurchaseValidatorTest`
- Account ID rules
- Empty/null requests
- Null entries
- Negative/zero ticket counts
- Max ticket limit
- Adult‑presence rule
- Valid combinations
- Infants less than adults rule

### ✔ `SeatReservationCalculatorTest`
- Adults only
- Children only
- Infants ignored
- Mixed types
- Empty input
- Singleton behaviour

### ✔ `TicketPriceCalculatorTest`
- Adults only
- Children only
- Infants
- Mixed types
- Empty input
- Singleton behaviour

**Note:**  
`ConfigurationProvider` is intentionally not unit‑tested because it is static, infrastructure‑level, and validated via integration.

---

## 📝 Configuration

`src/main/resources/application.properties`

Example for defining properties:\
ticket.max.count=25\
ticket.price.adult=25\
ticket.price.child=15\
ticket.price.infant=0

---

## 🪵 Logging

SLF4J + Logback is used for:

- High‑level business events (`INFO`)
- Internal calculations (`DEBUG`)
- Validation failures (`WARN`)
- Configuration loading (`INFO`)

A sample `logback.xml` may be added for custom formatting.

---

## 🧩 Design Principles

This project follows:

### ✔ **Single Responsibility Principle**
Each class has one clear purpose.

### ✔ **Dependency Injection**
`TicketServiceImpl` accepts injected dependencies for testability.

### ✔ **Stateless Domain Services**
Calculators are pure, deterministic, and singleton‑based.

### ✔ **Fail‑Fast Configuration**
Invalid config prevents the app from starting.

### ✔ **Testability**
All business logic is unit‑tested.

---

## 📦 Dependencies

- Java 21+
- Maven
- SLF4J
- Logback
- JUnit 5
- Mockito

---

## 🧾 Assumptions

The following assumptions were made during implementation:

1. **Infants do not require seats**  
   This is a business rule provided in the original requirements.
2. **Number of infants must not be more than the adults.**  
      As each infant is going to sit in an adult's lap, it is a logical assumption.
3. **Infants may have zero cost**  
   Pricing is fully controlled by configuration.
4. **At least one adult is required**  
   Children and infants cannot be purchased alone.
5. **Configuration is static and loaded once**  
   The application is expected to fail fast if configuration is invalid.
6. **External services (payment & seat reservation) always succeed**  
   They are treated as black‑box dependencies.
7. **TicketTypeRequest is trusted to contain valid enum values**  
   Only the count is validated.
8. **No concurrency concerns**  
   The service is stateless and thread‑safe.
9. **No partial failures**  
   If validation passes, both payment and seat reservation must succeed.
10. **Logging is required for observability**  
    All major steps are logged at appropriate levels.

---

## 🏁 Running Tests

mvn test


---

## 🎉 Summary

This project demonstrates a clean, production‑grade implementation of a ticket‑purchase workflow using:

- Pure Java
- Clean architecture
- Strong validation
- Stateless domain services
- Centralised configuration
- Comprehensive test coverage
- Logging for observability

It is intentionally simple, maintainable, and easy to extend — ideal for coding exercises, interviews, and real‑world service design.
