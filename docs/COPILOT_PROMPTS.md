# GitHub Copilot Prompts and Agents

This document captures example GitHub Copilot prompts used to guide design, development, testing, documentation, and code-review work for the Event Ledger project.

## Copilot Agent Links

- GitHub repository Agents tab: [`https://github.com/saichandra1324/Charles-Schwab/agents`](https://github.com/saichandra1324/Charles-Schwab/agents)
- Repository Copilot instructions: [`.github/copilot-instructions.md`](../.github/copilot-instructions.md)
- Custom Design Agent profile: [`.github/agents/design-agent.agent.md`](../.github/agents/design-agent.agent.md)
- Custom Development Agent profile: [`.github/agents/development-agent.agent.md`](../.github/agents/development-agent.agent.md)
- Custom QA Agent profile: [`.github/agents/qa-agent.agent.md`](../.github/agents/qa-agent.agent.md)
- Custom Code Review Agent profile: [`.github/agents/code-review-agent.agent.md`](../.github/agents/code-review-agent.agent.md)
- Custom Documentation Agent profile: [`.github/agents/documentation-agent.agent.md`](../.github/agents/documentation-agent.agent.md)
- Design Agent deliverable: [`docs/DESIGN_AGENT.md`](DESIGN_AGENT.md)
- Development Agent deliverable: [`docs/DEVELOPMENT_AGENT.md`](DEVELOPMENT_AGENT.md)
- QA Agent deliverable: [`docs/QA_AGENT.md`](QA_AGENT.md)
- AI-assisted SDLC summary: [`docs/AI_USAGE.md`](AI_USAGE.md)

## How to Show These Prompts in the GitHub Agents Tab

The GitHub Agents tab shows real Copilot agent sessions. This repository includes custom agent profiles under `.github/agents/`, which should become selectable from the agent dropdown after they are pushed to the default branch. Starting a task with one of those agents creates the visible session in the GitHub Agents tab.

To make sessions appear there:

1. Open [`https://github.com/saichandra1324/Charles-Schwab/agents`](https://github.com/saichandra1324/Charles-Schwab/agents).
2. Click **Create task**.
3. Select the `saichandra1324/Charles-Schwab` repository and the `main` branch.
4. Paste one prompt from this file.
5. Start the task and let Copilot create the agent session.

Suggested visible sessions:

- `Design Agent` - use the Design Agent prompt.
- `Development Agent` - use the Development Agent prompt.
- `QA Agent` - use the QA Agent prompt.
- `Code Review Agent` - use the Code Review Agent prompt.
- `Documentation Agent` - use the Documentation Agent prompt.

## Design Agent Prompt

```text
Act as a GitHub Copilot design agent for a Spring Boot distributed systems take-home project. Design an Event Ledger with a Gateway Service and Account Service, separate service-owned databases, idempotent event processing, out-of-order event history, trace propagation, audit trails, and graceful downstream failure handling. Keep the design realistic for a small assignment and include API contracts, data ownership, and failure scenarios.
```

## Development Agent Prompt

```text
Act as a GitHub Copilot development agent for this repository. Implement the next feature using existing Spring Boot patterns. Preserve service boundaries, do not add shared database access, keep idempotency based on eventId, propagate X-Trace-ID, use structured logging, and update or add focused tests for changed behavior.
```

## QA Agent Prompt

```text
Act as a GitHub Copilot QA agent. Review the Event Ledger requirements and generate unit, integration, functional, and negative test cases for duplicate event submission, duplicate account transactions, out-of-order event reads, validation failures, trace propagation, audit endpoints, and Account Service outage handling.
```

## Code Review Agent Prompt

```text
Act as a GitHub Copilot code-review agent. Review this diff for behavioral regressions, missing tests, incorrect HTTP status codes, broken idempotency, trace propagation gaps, transaction boundary issues, and service-boundary violations. Report findings by severity with file and line references.
```

## Documentation Agent Prompt

```text
Act as a GitHub Copilot documentation agent. Update the README and docs so a reviewer can understand the architecture, run the project locally, run tests, inspect audit endpoints, understand the AI-assisted SDLC workflow, and verify Docker Compose runtime behavior.
```

## Resiliency Agent Prompt

```text
Act as a resiliency-focused Copilot agent. Review the Gateway to Account Service call path. Confirm Retry and Circuit Breaker behavior, failure-to-503 mapping, local event persistence after downstream failure, audit logging, and tests that prove graceful degradation.
```

## Commit History Agent Prompt

```text
Act as a GitHub Copilot release assistant. Suggest small, realistic commit messages that show the project evolving from initial Spring Boot setup, to Account Service, Gateway ingestion, tracing, resiliency, tests, Docker runtime, and AI-assisted SDLC documentation.
```

## Human Review Notes

Copilot prompts were used to accelerate discovery, implementation, testing, and documentation. Final code and architecture decisions were reviewed manually to keep the implementation aligned with the assignment requirements.
