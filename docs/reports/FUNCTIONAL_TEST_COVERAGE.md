# Functional Test Coverage Report

This report maps the Event Ledger functional requirements to automated tests and verification commands.

## Requirement Traceability Matrix

| Requirement | Verification | Test Class / Command | Expected Result |
|---|---|---|---|
| Submit transaction event | Automated functional controller test | `GatewayFunctionalTests.createsAndRetrievesEventThroughHttpApi` | `POST /events` returns `201 Created` and stores the event |
| Retrieve event by ID | Automated functional controller test | `GatewayFunctionalTests.createsAndRetrievesEventThroughHttpApi` | `GET /events/{id}` returns the submitted event |
| List events by account chronologically | Automated service/functional test | `EventServiceTests.listEventsOrderedByEventTimestamp` | Events are sorted by `eventTimestamp` ascending |
| Duplicate event idempotency | Automated service/functional test | `EventServiceTests.duplicateEventDoesNotCallAccountServiceAgain` and `GatewayFunctionalTests.duplicatePostReturnsExistingEventWithoutSecondAccountCall` | Duplicate submission does not call Account Service twice |
| Balance calculation | Automated service test | `AccountServiceTests.appliesCreditsAndDebitsAndPreventsDuplicateTransaction` | Balance equals credits minus debits |
| Account transaction API | Automated functional controller test | `AccountControllerFunctionalTests.appliesTransactionAndReturnsBalanceThroughHttpApi` | Account Service accepts transaction and balance is updated |
| Validation: invalid event type/amount | Automated controller test | `EventControllerValidationTests.rejectsInvalidPayload` | Gateway returns `400 Bad Request` |
| Validation: accountId mismatch | Automated controller test | `AccountControllerFunctionalTests.rejectsAccountIdMismatch` | Account Service returns `400 Bad Request` |
| Trace propagation response header | Automated controller test | `TraceFilterTests.responseIncludesTraceIdHeader` | Response contains the same `X-Trace-ID` |
| Trace propagation to downstream client | Unit test | `FeignConfigTests.propagatesTraceIdFromMdcToFeignHeader` | Feign request contains `X-Trace-ID` |
| Downstream Account Service failure | Automated functional controller test | `GatewayFunctionalTests.accountServiceFailureReturnsServiceUnavailable` | Gateway returns `503 Service Unavailable` |
| Graceful local reads during downstream outage | Automated functional controller test | `GatewayFunctionalTests.localEventReadWorksAfterDownstreamFailure` | Event read endpoint remains available from Gateway database |
| Auditability | Automated functional controller test | `GatewayFunctionalTests.auditEndpointReturnsEventAudit` | Gateway audit endpoint returns audit records for event processing |
| Health checks | Manual verification | `curl http://localhost:8080/health` and `curl http://localhost:8081/health` | Services return status and DB connectivity |
| Metrics | Manual verification | `curl http://localhost:8080/actuator/prometheus` and `curl http://localhost:8081/actuator/prometheus` | Prometheus metrics are exposed |

## Local Functional Verification Commands

Start both services:

```bash
docker compose up --build
```

Submit a credit event:

```bash
curl -i -X POST http://localhost:8080/events \
  -H 'Content-Type: application/json' \
  -H 'X-Trace-ID: demo-trace-001' \
  -d '{
    "eventId": "evt-001",
    "accountId": "acct-123",
    "type": "CREDIT",
    "amount": 150.00,
    "currency": "USD",
    "eventTimestamp": "2026-05-15T14:02:11Z",
    "metadata": {"source": "mainframe-batch", "batchId": "B-9042"}
  }'
```

Submit a debit event out of chronological order:

```bash
curl -i -X POST http://localhost:8080/events \
  -H 'Content-Type: application/json' \
  -d '{
    "eventId": "evt-000",
    "accountId": "acct-123",
    "type": "DEBIT",
    "amount": 25.00,
    "currency": "USD",
    "eventTimestamp": "2026-05-15T13:00:00Z"
  }'
```

Verify chronological event listing:

```bash
curl -s 'http://localhost:8080/events?account=acct-123'
```

Verify balance:

```bash
curl -s http://localhost:8080/accounts/acct-123/balance
```

Submit duplicate event:

```bash
curl -i -X POST http://localhost:8080/events \
  -H 'Content-Type: application/json' \
  -d '{
    "eventId": "evt-001",
    "accountId": "acct-123",
    "type": "CREDIT",
    "amount": 150.00,
    "currency": "USD",
    "eventTimestamp": "2026-05-15T14:02:11Z"
  }'
```

Expected duplicate behavior: original event is returned and balance is not applied again.
