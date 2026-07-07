# Architecture and Design Diagrams

## High-Level Architecture

```mermaid
flowchart TD
    Client[Browser / Client / Upstream System] -->|POST /events| Gateway[Event Gateway API]
    Gateway -->|Persist event records| GatewayDB[(Gateway H2 DB)]
    Gateway -->|REST call with X-Trace-ID| Account[Account Service]
    Account -->|Persist accounts and transactions| AccountDB[(Account H2 DB)]

    Gateway -->|/actuator/health / /health| GatewayHealth[Gateway Health]
    Account -->|/actuator/health / /health| AccountHealth[Account Health]
    Gateway -->|JSON logs + metrics| Observability[Observability]
    Account -->|JSON logs + metrics| Observability
```

## Service Boundary Diagram

```mermaid
flowchart LR
    subgraph Public[Public Boundary]
        Client[Client]
        Gateway[Event Gateway API]
    end

    subgraph Internal[Internal Boundary]
        Account[Account Service]
    end

    subgraph DataStores[Independent Data Stores]
        GatewayDB[(Gateway H2 DB)]
        AccountDB[(Account H2 DB)]
    end

    Client --> Gateway
    Gateway --> GatewayDB
    Gateway --> Account
    Account --> AccountDB
```

## Event Processing Sequence

```mermaid
sequenceDiagram
    participant Client
    participant Gateway as Event Gateway API
    participant GatewayDB as Gateway DB
    participant Account as Account Service
    participant AccountDB as Account DB

    Client->>Gateway: POST /events
    Gateway->>Gateway: Validate payload
    Gateway->>Gateway: Generate/reuse X-Trace-ID
    Gateway->>GatewayDB: Check eventId

    alt Duplicate event
        Gateway-->>Client: 200 OK existing event
    else New event
        Gateway->>GatewayDB: Save event as RECEIVED
        Gateway->>Account: POST transaction + X-Trace-ID
        Account->>AccountDB: Save transaction and update balance
        Account-->>Gateway: 201/200 transaction response
        Gateway->>GatewayDB: Update event status to APPLIED
        Gateway-->>Client: 201 Created
    end
```

## Failure and Circuit Breaker Sequence

```mermaid
sequenceDiagram
    participant Client
    participant Gateway as Event Gateway API
    participant Resilience as Retry + Circuit Breaker
    participant Account as Account Service
    participant GatewayDB as Gateway DB

    Client->>Gateway: POST /events
    Gateway->>GatewayDB: Save event as RECEIVED
    Gateway->>Resilience: Call Account Service
    Resilience->>Account: Attempt 1
    Account--xResilience: Timeout/failure
    Resilience->>Account: Attempt 2
    Account--xResilience: Timeout/failure
    Resilience->>Account: Attempt 3
    Account--xResilience: Timeout/failure
    Resilience->>Resilience: Open circuit if threshold reached
    Resilience-->>Gateway: Dependency unavailable
    Gateway->>GatewayDB: Mark FAILED_ACCOUNT_SERVICE_UNAVAILABLE
    Gateway-->>Client: 503 Service Unavailable
```

## Trace Propagation Diagram

```mermaid
flowchart LR
    Client[Client Request] -->|optional X-Trace-ID| Gateway[Gateway Trace Filter]
    Gateway -->|store traceId in MDC| GatewayLogs[Gateway JSON Logs]
    Gateway -->|Feign interceptor adds X-Trace-ID| Account[Account Trace Filter]
    Account -->|store traceId in MDC| AccountLogs[Account JSON Logs]
```

## Data Model Diagram

```mermaid
erDiagram
    GATEWAY_EVENT {
        string eventId PK
        string accountId
        string type
        decimal amount
        string currency
        datetime eventTimestamp
        string metadataJson
        string status
        datetime createdAt
    }

    ACCOUNT {
        string accountId PK
        decimal balance
    }

    TRANSACTION_RECORD {
        long id PK
        string eventId UK
        string accountId
        string type
        decimal amount
        string currency
        datetime eventTimestamp
    }

    ACCOUNT ||--o{ TRANSACTION_RECORD : owns
```
