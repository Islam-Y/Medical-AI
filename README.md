# Medical-AI

Medical-AI - backend MVP персональной медицинской AI-платформы. Сервис хранит медицинскую историю пользователя, принимает документы и анализы, запускает AI-анализ, строит исследовательский индекс для поиска по медицинскому контексту и предоставляет чатовый интерфейс, готовый к RAG.

Проект задуман не как "AI-врач", а как персональная медицинская память и интеллектуальный навигатор: система помогает собрать контекст, увидеть динамику, подготовить вопросы врачу и в дальнейшем сравнивать алгоритмы нейросетевого информационного поиска на реальных сценариях медицинского retrieval.

В проекте исследуется прототип для персонального медицинского анализа, RAG-ready чата и сравнения sparse/dense/hybrid retrieval-подходов на медицинских документах пользователя.
В исследование входит:
- постановка исследовательской задачи на прикладном домене: поиск по анализам, симптомам, заключениям, чату и временной медицинской истории;
- экспериментальная архитектура для сравнения BM25, sparse neural, dense и hybrid retrieval;
- benchmark artifacts: evaluation runs, метрики качества поиска, latency, throughput, размер индекса;
- исследование trade-off между retrieval quality и latency/throughput;
- связь с distributed systems: микросервисы, Kafka choreography, outbox, отдельные доменные БД;
- задел под intelligent medical context analysis без включения диагностических обещаний в MVP.

## Что входит в MVP

- REST API для регистрации, логина, профиля, медицинских записей, документов, AI-задач, чата, поиска, индексации, evaluation, consent, audit и уведомлений.
- Kafka choreography между сервисами без центрального orchestrator.
- Transactional outbox для надежной публикации событий.
- Postgres per service для metadata, users, health facts, consent, audit и service state.
- S3-compatible object storage для оригинальных PDF/JPEG/PNG и canonical extraction artifacts.
- OpenSearch как retrieval/index storage для chunks, BM25/sparse search и latency/throughput экспериментов.
- Liquibase release-changelog layout, без Flyway.
- JWT auth: `auth` выпускает HMAC JWT, остальные сервисы валидируют токен.
- AI boundary: отдельный `ai-analysis` microservice со stub-адаптером, готовый к замене на OCR/VLM/LLM pipeline.
- RAG boundary: `chat`, `retrieval`, `indexing`, `evaluation`.
- Unit-тесты и JaCoCo coverage gate `>= 80%` по сервисным модулям.

## Архитектура

Основной продуктовый поток:

```text
document-ingestion
  -> S3 original object + Postgres metadata
  -> Kafka DocumentUploaded
  -> ai-analysis
  -> S3 canonical Markdown/Layout JSON artifact + Postgres extraction metadata
  -> Kafka DocumentExtractionCompleted
  -> health-record normalized facts
  -> indexing chunks/provenance/sparse terms
  -> OpenSearch
  -> retrieval
  -> chat
```

Событийная модель:

```text
auth/user/document/health/chat/analysis/retrieval/indexing/evaluation/consent/audit/notification
  -> transactional outbox
  -> Kafka topics
  -> subscribing services
  -> local DB update
```

Каждый сервис владеет своей БД и не ходит напрямую в таблицы другого сервиса. Связь между доменами идет через REST API для синхронных запросов и Kafka events для асинхронной хореографии.

Storage split:

```text
MinIO/S3:
- original files: PDF, JPEG, PNG, scans
- derived artifacts: canonical Markdown, layout JSON, extracted tables

Postgres:
- document metadata
- extraction metadata
- normalized medical facts
- consent/audit/state

OpenSearch:
- searchable chunks
- sparse terms
- BM25/search index
- source provenance for retrieval results
```

## Модули

| Module | Responsibility |
| --- | --- |
| `event-contracts` | Общий event envelope, topic names и payload records. |
| `auth` | Регистрация, логин, выпуск JWT, `/api/v1/auth/me`. |
| `user` | Профиль пользователя и персональные параметры. |
| `health-record` | Наблюдения, симптомы, диагнозы, timeline. |
| `document-ingestion` | Загрузка документов, S3 object reference, checksum, document metadata. |
| `ai-analysis` | AI analysis jobs, canonical Markdown/Layout JSON artifacts, stub adapter под будущий OCR/VLM/LLM pipeline. |
| `chat` | Медицинские чат-сессии и сообщения, LLM-ready adapter boundary. |
| `retrieval` | Search API для BM25, sparse neural, dense и hybrid retrieval экспериментов поверх OpenSearch-compatible boundary. |
| `indexing` | Чтение extraction artifacts, chunks, sparse representation и запись в OpenSearch-compatible index. |
| `evaluation` | Benchmark runs и метрики качества/latency/throughput/index size. |
| `consent` | Согласия пользователя на AI-анализ, research evaluation, doctor access и export. |
| `audit` | Аудит действий и доступов как база для коммерческой безопасности. |
| `notification` | Уведомления пользователя и реакция на health-record events. |

## API surface

| Service | Endpoints |
| --- | --- |
| `auth` | `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `GET /api/v1/auth/me` |
| `user` | `GET /api/v1/users/me`, `PATCH /api/v1/users/me` |
| `health-record` | `POST/GET /api/v1/observations`, `POST/GET /api/v1/symptoms`, `POST/GET /api/v1/diagnoses`, `GET /api/v1/timeline` |
| `document-ingestion` | `POST /api/v1/documents`, `GET /api/v1/documents`, `GET /api/v1/documents/{id}` |
| `ai-analysis` | `GET /api/v1/analysis/jobs`, `GET /api/v1/analysis/jobs/{id}` |
| `chat` | `POST /api/v1/chat/sessions`, `GET /api/v1/chat/sessions`, `GET/POST /api/v1/chat/sessions/{sessionId}/messages` |
| `retrieval` | `POST /api/v1/retrieval/search` |
| `indexing` | `POST /api/v1/index/entries`, `GET /api/v1/index/entries`, `GET /api/v1/index/entries/{id}` |
| `evaluation` | `POST /api/v1/evaluation/runs`, `GET /api/v1/evaluation/runs`, `GET /api/v1/evaluation/runs/{id}` |
| `consent` | `POST /api/v1/consents`, `GET /api/v1/consents`, `PATCH /api/v1/consents/{id}/revoke` |
| `audit` | `POST /api/v1/audit/events`, `GET /api/v1/audit/events` |
| `notification` | `GET /api/v1/notifications`, `PATCH /api/v1/notifications/{id}/read` |

## Kafka topics

- `medic.auth.events.v1`
- `medic.user.events.v1`
- `medic.document.events.v1`
- `medic.health-record.events.v1`
- `medic.chat.events.v1`
- `medic.retrieval.events.v1`
- `medic.indexing.events.v1`
- `medic.evaluation.events.v1`
- `medic.consent.events.v1`
- `medic.audit.events.v1`
- `medic.notification.events.v1`

Event envelope содержит `eventId`, `eventType`, `eventVersion`, `occurredAt`, `correlationId`, `userId`, `payload`.

## Local stack

Требования:

- Java 21
- Docker
- Gradle wrapper из репозитория

Запуск инфраструктуры:

```bash
docker compose up -d
```

Проверка:

```bash
./gradlew clean test jacocoTestCoverageVerification
```

Локальные порты:

| Service | Port |
| --- | --- |
| Postgres | `5432` |
| Kafka | `9092` |
| MinIO S3 API | `9000` |
| MinIO Console | `9001` |
| OpenSearch | `9200` |
| OpenSearch Performance Analyzer | `9600` |
| Auth | `8081` |
| User | `8082` |
| Health record | `8083` |
| Document ingestion | `8084` |
| AI analysis | `8085` |
| Chat | `8086` |
| Notification | `8087` |
| Retrieval | `8088` |
| Indexing | `8089` |
| Evaluation | `8090` |
| Consent | `8091` |
| Audit | `8092` |

Postgres credentials для local dev: `medic` / `medic`.

MinIO credentials для local dev: `medicadmin` / `medicadmin123`.

Default buckets:

- `medical-ai-documents` для оригиналов;
- `medical-ai-extractions` для canonical artifacts.

Default OpenSearch index: `medical-ai-chunks`.

## Database migrations

Каждый сервис с БД использует Liquibase:

```text
src/main/resources/liquibase/changelog/changelog.yml
src/main/resources/liquibase/changelog/0.0.1/changelog-0.0.1.yml
src/main/resources/liquibase/changelog/0.0.1/ddl/*.sql
src/main/resources/liquibase/changelog/0.0.1/ddl/*-rollback.sql
```

## Research roadmap

- подключить реальные sparse embeddings и inverted/sparse index backend;
- добавить dense vector backend для baseline;
- реализовать hybrid retrieval и reranking;
- хранить query sets и relevance judgments для evaluation;
- считать NDCG, MRR, Recall@K, Precision@K, latency p50/p95/p99, throughput и index size;
- сравнивать retrieval quality с latency/throughput trade-offs;
- добавить reproducible benchmark profiles для локальных и серверных прогонов.

## Commercial roadmap

- end-to-end encryption для пользовательских документов;
- policy-based access control;
- audit export;
- consent versioning;
- user data export/delete workflows;
- doctor-reviewed report workflow;
- mobile/web UI;
- production OCR/VLM/LLM adapters;
- compliance review перед любыми медицинскими claims.
