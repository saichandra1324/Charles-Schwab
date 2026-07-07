# QA Agent Deliverables

## Purpose

The QA Agent demonstrates how AI-assisted testing was used to improve confidence in the Event Ledger solution. The QA work focuses on requirement traceability, automated unit tests, functional tests, and repeatable coverage reporting.

## QA Agent Responsibilities

The QA Agent was used to accelerate the following activities:

1. Convert assignment requirements into test scenarios.
2. Identify positive, negative, duplicate, out-of-order, trace propagation, and failure-path test cases.
3. Generate baseline JUnit test classes.
4. Review service logic for missing edge cases.
5. Produce coverage report instructions and functional coverage documentation.

## Automated Test Scope

### Unit Tests

| Area | Test Coverage |
|---|---|
| Account balance calculation | Credits increase balance, debits decrease balance |
| Account idempotency | Duplicate `eventId` does not apply twice |
| Gateway idempotency | Duplicate `eventId` returns existing event and avoids second Account Service call |
| Out-of-order events | Gateway returns account events ordered by `eventTimestamp` |
| Account Service failure | Gateway converts downstream failures into `503`-style domain exception |
| Validation | Invalid event payloads are rejected with `400 Bad Request` |
| Trace filter | `X-Trace-ID` is returned on Gateway responses |
| Feign trace propagation | Gateway forwards trace ID to downstream Account Service calls |

### Functional Tests

| Functional Flow | Coverage |
|---|---|
| Event submission | `POST /events` accepts valid payload and returns `201 Created` |
| Event retrieval | `GET /events/{id}` returns stored event |
| Event listing | `GET /events?account=...` returns chronological events |
| Duplicate event submission | Second POST with same `eventId` returns existing event and does not reapply balance |
| Account Service unavailable | Gateway returns a clear service-unavailable response instead of hanging |
| Account transaction API | Account Service applies transactions through REST controller |
| Account validation | Account Service rejects invalid transaction/account mismatch |

## Test Commands

Run all tests:

```bash
mvn clean test
```

Run only Gateway tests:

```bash
mvn -pl gateway-service test
```

Run only Account Service tests:

```bash
mvn -pl account-service test
```

## Unit Test Coverage Report

JaCoCo is configured in the Maven build. After running `mvn clean test`, open:

```text
gateway-service/target/site/jacoco/index.html
account-service/target/site/jacoco/index.html
```

The script below copies the generated reports into the documentation folder:

```bash
./scripts/generate-coverage-reports.sh
```

Expected output folders:

```text
docs/reports/unit-coverage/gateway-service/index.html
docs/reports/unit-coverage/account-service/index.html
```

## Functional Test Coverage Report

Functional coverage is documented as a requirement traceability matrix in:

```text
docs/reports/FUNCTIONAL_TEST_COVERAGE.md
```

This report maps assignment requirements to automated test classes and manual verification commands.

## QA Agent Prompt Examples

### Scenario generation prompt

```text
Act as a QA agent for a Spring Boot distributed systems take-home project. Generate unit, integration, and negative test cases for idempotency, out-of-order events, trace propagation, and Account Service failure handling.
```

### Test review prompt

```text
Review these test cases against the Event Ledger requirements. Identify missing edge cases around duplicate event submission, downstream service unavailability, invalid payloads, and chronological ordering.
```

### Coverage prompt

```text
Create a coverage traceability matrix mapping each Event Ledger requirement to a JUnit test class, expected behavior, and pass/fail evidence.
```

## Notes

The AI-generated test ideas were reviewed and refined manually. The final tests are intended to prove business behavior, not just increase coverage percentages.
