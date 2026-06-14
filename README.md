# Medical-AI

Backend MVP for a personal medical record core.

## Modules

- `event-contracts`: shared Kafka event envelope, topics, and payload records.
- `auth`: registration, login, HMAC JWT, `/api/v1/auth/me`.
- `user`: user profile and personal parameters, `/api/v1/users/me`.
- `health-record`: observations, symptoms, diagnoses, timeline.
- `document-ingestion`: document upload and local file storage.
- `ai-analysis`: AI analysis job boundary; consumes document upload events and emits extraction results.
- `chat`: medical chat sessions and messages with an LLM-ready adapter boundary.
- `retrieval`: search API for BM25, sparse neural, dense, and hybrid retrieval experiments.
- `indexing`: sparse representation and index-entry boundary fed by document, health-record, and chat events.
- `evaluation`: benchmark runs and metrics for search quality, latency, throughput, and index size.
- `consent`: user consent scopes for AI analysis, research evaluation, doctor access, and export.
- `audit`: user-visible audit trail foundation for commercial security requirements.
- `notification`: user notifications and health-record event rules.

## Architecture Intent

Product flow:

`document-ingestion` -> `ai-analysis` -> `health-record` -> `indexing` -> `retrieval` -> `chat`

Research layer:

- `indexing` stores sparse index entries and listens to document, health-record, and chat events.
- `retrieval` exposes BM25, sparse neural, dense, and hybrid retrieval modes as switchable experiment targets.
- `evaluation` records benchmark runs with search quality, latency, throughput, and index-size metrics.

Commercial foundation:

- `consent` stores explicit user consent for AI analysis, research evaluation, doctor access, and export.
- `audit` stores user-visible access/action events for security and future compliance workflows.

## Local stack

```bash
docker compose up -d
./gradlew clean test jacocoTestCoverageVerification
```

Default local services:

- Postgres: `localhost:5432`, user/password `medic`/`medic`
- Kafka: `localhost:9092`
- Auth service: `localhost:8081`
- User service: `localhost:8082`
- Health record service: `localhost:8083`
- Document ingestion service: `localhost:8084`
- AI analysis service: `localhost:8085`
- Chat service: `localhost:8086`
- Notification service: `localhost:8087`
- Retrieval service: `localhost:8088`
- Indexing service: `localhost:8089`
- Evaluation service: `localhost:8090`
- Consent service: `localhost:8091`
- Audit service: `localhost:8092`

Each service uses Liquibase release changelogs under `src/main/resources/liquibase/changelog/0.0.1`.
