# GitHub Copilot Agent Instructions

Use these instructions when GitHub Copilot Chat, Copilot Agent mode, or another AI coding agent works in this repository.

## Project Context

This repository implements an Event Ledger system with two Spring Boot services:

- `gateway-service`: accepts client events, stores the event ledger, enforces gateway-level idempotency, calls Account Service, and exposes audit/history endpoints.
- `account-service`: applies financial transactions idempotently, owns account balances, and exposes account/audit endpoints.

The services intentionally use separate H2 databases to preserve service ownership boundaries. Do not introduce shared database access between services.

## Agent Guardrails

- Keep Gateway and Account Service domain models separate.
- Preserve idempotency at both layers:
  - Gateway idempotency is based on `eventId`.
  - Account Service idempotency is based on transaction `eventId`.
- Preserve HTTP semantics:
  - New event ingestion returns `201 Created`.
  - Duplicate event ingestion returns `200 OK`.
  - Validation failures return `400 Bad Request`.
  - Missing events return `404 Not Found`.
  - downstream Account Service failures return `503 Service Unavailable`.
- Keep `X-Trace-ID` propagation working across both services.
- Use structured JSON logging and MDC trace IDs consistently.
- Add or update tests when changing controller, service, idempotency, tracing, audit, or resiliency behavior.
- Run `mvn clean test` before considering work complete.

## Preferred Prompt Pattern

When making changes, ask Copilot to work as a focused agent:

```text
Act as a GitHub Copilot coding agent for this Spring Boot Event Ledger repo. Read the current service boundaries first, make the smallest safe change, preserve idempotency and trace propagation, update tests when behavior changes, and verify with mvn clean test.
```

## Review Checklist

Before accepting generated code, verify:

- No cross-service repository/entity coupling was added.
- Error responses still include `traceId`.
- Duplicate events do not call Account Service a second time.
- Account balances are changed exactly once per unique transaction.
- New docs and examples match the current API paths.
