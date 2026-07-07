# AI-Assisted SDLC Usage

This project intentionally demonstrates AI-augmented software engineering practices across design, development, testing, and documentation.

## Design Agent

AI was used to accelerate design discovery and validation:

- Generated initial microservice boundary options
- Confirmed that Gateway and Account Service should own separate databases
- Identified idempotency requirements at both Gateway and Account Service layers
- Produced Mermaid architecture and sequence diagrams
- Helped define API contracts and failure scenarios
- Reviewed resiliency options and selected Retry + Circuit Breaker as the best fit for the time-boxed assignment

Human review was applied to keep the design simple, realistic, and aligned with the assignment requirements.

## Development Agent

AI was used as a coding assistant for implementation acceleration:

- Generated Spring Boot boilerplate for controllers, services, repositories, DTOs, and entities
- Suggested validation annotations for request payloads
- Assisted with structured logging and trace propagation using MDC
- Generated Resilience4j configuration for retry and circuit breaker
- Helped create error response mapping through `@RestControllerAdvice`
- Suggested meaningful Git commit messages to reflect working progress

The final code was manually reviewed to ensure business rules were explicit and readable.

## QA Agent

AI was used to identify and generate test scenarios:

- Duplicate event submission should not alter balance twice
- Out-of-order events should be returned by event timestamp
- Invalid payloads should return HTTP 400
- Account Service failures should map to HTTP 503
- Trace ID should be returned and propagated
- Balance should reflect credit minus debit

JaCoCo is configured to generate unit test coverage reports after running Maven tests.

## Example Prompts Used

- "Generate a clean Spring Boot microservice design for an event ledger with idempotency and out-of-order events."
- "Create a sequence diagram for Gateway to Account Service trace propagation."
- "Suggest unit tests for duplicate financial transaction event handling."
- "Review this resiliency approach and explain why Retry + Circuit Breaker is appropriate."
- "Generate meaningful commit messages that show AI-assisted SDLC progress."

## Engineering Principle

AI was used to accelerate repetitive and exploratory tasks, but final engineering decisions were reviewed manually. The goal was not to blindly generate code, but to use AI as a productivity multiplier across the SDLC.
