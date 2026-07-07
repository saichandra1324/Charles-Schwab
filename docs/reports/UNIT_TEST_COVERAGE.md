# Unit Test Coverage Report

## How to Generate

Run:

```bash
mvn clean test
```

Then open the JaCoCo HTML reports:

```text
gateway-service/target/site/jacoco/index.html
account-service/target/site/jacoco/index.html
```

To copy the HTML reports into `docs/reports/unit-coverage`, run:

```bash
./scripts/generate-coverage-reports.sh
```

## Unit Test Inventory

| Module | Test Class | Purpose |
|---|---|---|
| account-service | `AccountServiceTests` | Balance calculation and duplicate transaction protection |
| account-service | `AccountControllerFunctionalTests` | REST controller behavior, validation, balance endpoint |
| gateway-service | `EventServiceTests` | Idempotency, out-of-order listing, downstream failure translation |
| gateway-service | `EventControllerValidationTests` | Request validation and error response behavior |
| gateway-service | `TraceFilterTests` | Trace ID response behavior |
| gateway-service | `FeignConfigTests` | Trace ID propagation from Gateway to Account Service |
| gateway-service | `GatewayFunctionalTests` | Functional API behavior with mocked Account Service dependency |

## Coverage Expectations

The target is practical business coverage rather than 100% line coverage.

| Area | Target |
|---|---:|
| Service-layer business logic | 80%+ |
| Controller validation/failure handling | 70%+ |
| DTO/entity boilerplate | Best effort |
| Configuration classes | Covered where behavior matters, such as trace propagation |

## Evidence

Because this repository is intended to be run locally with Maven, the authoritative coverage evidence is the generated JaCoCo HTML report under each module's `target/site/jacoco` directory.

Attach screenshots or exported HTML folders from the generated reports when submitting if the hiring team requests visual evidence.
