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
- `notification`: user notifications and health-record event rules.

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

Each service uses Liquibase release changelogs under `src/main/resources/liquibase/changelog/0.0.1`.
