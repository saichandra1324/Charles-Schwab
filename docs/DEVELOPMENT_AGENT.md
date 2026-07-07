# Development AI Agent

## Purpose

The Development AI Agent was used to accelerate implementation work while keeping the final engineering decisions under human review. The agent focused on production-readiness concerns that are easy to miss in a small take-home exercise: consistent error handling, structured logging, audit trails, and meaningful Git history.

## Scope of Work

### Error Handling

The Development Agent helped implement a consistent exception strategy across both services:

- Validation failures return `400 Bad Request` with a clear message.
- Missing Gateway events return `404 Not Found`.
- Account Service dependency failures return `503 Service Unavailable` from the Gateway.
- Error responses include `timestamp`, `traceId`, HTTP status, error reason, and message.
- Exceptions are logged through the centralized `GlobalExceptionHandler`.

This keeps API behavior predictable and makes operational troubleshooting easier.

### Structured Logging

Both services use JSON logs through Logback configuration. Logs include:

- Timestamp
- Service name
- Log level
- Logger name
- Message
- Trace ID from MDC

The Gateway creates or reuses `X-Trace-ID`, stores it in MDC, sends it back to the client, and propagates it to the Account Service. The Account Service reads the same header and logs the same trace ID, allowing one request to be followed across both services.

### Auditing Capabilities

The Development Agent added persistent audit records to each service using each service's own H2 database.

Gateway audit actions:

- `EVENT_RECEIVED`
- `EVENT_DUPLICATE`
- `EVENT_APPLIED`
- `EVENT_APPLY_FAILED`

Account Service audit actions:

- `TRANSACTION_APPLIED`
- `TRANSACTION_DUPLICATE`
- `BALANCE_READ`

Each audit record captures:

- Action
- Event ID
- Account ID
- Trace ID
- Outcome
- Details
- Created timestamp

Audit endpoints were added for operational review:

Gateway:

```http
GET /audit
GET /events/{id}/audit
```

Account Service:

```http
GET /accounts/audit
GET /accounts/audit/events/{eventId}
```

### Meaningful Commit History

The implementation was organized into small commits that show a realistic engineering workflow:

1. Initialize multi-module Spring Boot project
2. Implement Account Service domain and APIs
3. Implement Gateway event ingestion and idempotency
4. Add trace propagation and structured logging
5. Add resilience patterns for Account Service calls
6. Add automated tests for core behavior
7. Add Design AI Agent deliverables
8. Add Development AI Agent error handling, logging, and auditing

## Human Review Applied

AI output was not accepted blindly. The generated implementation was reviewed for:

- Correct service boundaries
- No shared database or shared state
- Idempotency at both Gateway and Account Service layers
- Correct HTTP semantics
- Trace propagation consistency
- Minimal and useful audit data without storing unnecessary PII
- Testability and maintainability

## Interview Explanation

I used the Development AI Agent as a coding accelerator, not as a replacement for engineering judgment. It helped generate repetitive implementation pieces such as global exception handlers, audit entities, repositories, and structured logging patterns. I reviewed and refined the output to ensure it matched the distributed-systems requirements, especially idempotency, traceability, and graceful failure handling.
