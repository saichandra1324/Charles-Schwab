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

## Prompt Log

Representative prompts used during the project:

### Design Prompts

- "Design a two-service Spring Boot event ledger with a public Gateway API and an internal Account Service. Include idempotent event processing, separate databases, and out-of-order event listing."
- "Create a Mermaid architecture diagram showing client requests, Gateway persistence, Account Service persistence, trace propagation, logging, and metrics."
- "Create a sequence diagram for `POST /events` showing duplicate detection, Account Service transaction application, and `X-Trace-ID` propagation."
- "Review whether Retry + Circuit Breaker is an appropriate resiliency pattern for the Gateway to Account Service call in a time-boxed assignment."

### Development Prompts

- "Generate Spring Boot controller, service, repository, DTO, and JPA entity structure for the Account Service transaction and balance APIs."
- "Generate the Gateway event ingestion flow with request validation, event persistence, duplicate `eventId` handling, and Account Service REST integration."
- "Add MDC-based structured logging so `X-Trace-ID` appears in JSON logs for both services."
- "Suggest Micrometer counters for created events, duplicate events, applied transactions, and duplicate transactions."

### QA Prompts

- "Suggest JUnit tests for duplicate financial transaction event handling so the same event does not alter balance twice."
- "Create controller validation test cases for invalid event payloads returning HTTP 400."
- "Create a failure-path test for Account Service unavailability that expects Gateway to return HTTP 503."
- "Check whether the Account Service test proves credit, debit, and duplicate transaction behavior."

### Documentation Prompts

- "Write a README section that explains local run commands, Docker Compose startup, service endpoints, and test coverage report locations."
- "Generate a concise design document explaining service responsibilities, data model, trace propagation, resiliency, graceful degradation, and testing strategy."
- "Generate meaningful commit messages that show AI-assisted SDLC progress across setup, implementation, resiliency, observability, testing, Docker, and documentation."

## Engineering Principle

AI was used to accelerate repetitive and exploratory tasks, but final engineering decisions were reviewed manually. The goal was not to blindly generate code, but to use AI as a productivity multiplier across the SDLC.

LLM model used for this project: OpenAI GPT-5 via Codex.
You can document it as:
AI assistant/model used: OpenAI GPT-5 through Codex
Usage scope: design assistance, code generation, test generation, debugging, documentation, and prompt refinement
Human review: all generated outputs were reviewed and adjusted before inclusion

For this project, OpenAI GPT-5 via Codex was not integrated as a runtime dependency inside the Spring Boot services. Instead, it was used as an AI-assisted development tool during the SDLC.
You can describe it like this:
This project used OpenAI GPT-5 via Codex as an AI-assisted software engineering model to accelerate the design, implementation, testing, and documentation of a Java Spring Boot microservice system.

The Event Ledger application itself is built with Java 17 and Spring Boot. It contains two independently runnable services: the Event Gateway API and the Account Service. GPT-5 via Codex was used outside the application runtime to help generate and refine Spring Boot code, REST API structures, DTOs, JPA entities, validation rules, service-layer logic, tests, resiliency patterns, and documentation.

Codex helped translate project requirements into implementation artifacts such as Spring controllers, service classes, repository interfaces, request/response DTOs, validation annotations, structured logging, trace propagation, and JUnit test cases. Human review was applied to ensure that the generated code matched the business rules for idempotent financial event processing, duplicate prevention, balance calculation, and failure handling.

There is no direct API call from the Spring Boot application to OpenAI at runtime. The LLM was used as a development accelerator, not as part of the deployed application architecture.