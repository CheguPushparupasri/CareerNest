# CareerNest

CareerNest is an in-progress student career management application. It provides account management and tools for organizing projects, resumes, certifications, learning courses, internship applications, coding profiles, career timeline entries, and in-app notifications. It is **not production-ready**; see the status and limitations below.

## Implementation Status

### Implemented in the Current Source

- JWT login, refresh-token rotation and revocation, session management, BCrypt password hashing, role checks, signup, reset and email-verification token workflows.
- Student-owned project, resume, certification, learning-course, internship-application, coding-profile, and timeline CRUD APIs with user ownership checks.
- Dashboard aggregates derived from saved records, plus recent activity, project/certification summaries, and upcoming dated timeline/application items.
- In-app notification list, unread count, read/read-all, delete, and notifications emitted for selected record-creation actions.
- Admin overview, user listing, and audit-log APIs; Admin frontend pages are present.
- Profile basics and optional Cloudinary photo/project-screenshot upload code.

### Partially Implemented or Limited

- Email delivery has no configured provider. The provider interface and token workflows exist, but verification and password-recovery messages are not delivered until an `EmailDeliveryProvider` is supplied.
- Cloudinary is disabled by default. Upload requests return HTTP 503 unless it is enabled and configured.
- Resume sections are stored as structured JSON in the existing resume content field. The preview can use the browser print dialog; the backend does not generate a PDF file. The ATS score is a simple keyword heuristic, not a validated ATS service.
- Coding profiles and practice sessions are manually entered. There is no live synchronization with GitHub, LeetCode, HackerRank, or other platforms; difficulty-wise totals are not stored as structured data.
- Profile skills and career interests, certificate credential IDs/files, learning URLs/difficulty/date fields, and a distinct internship interview date are not represented in the current database schema.
- Some frontend workflows and external-service integrations have not been verified end to end against production services.

### Planned or Not Implemented

- A real email delivery provider, external coding-platform synchronization, AI model integration, server-generated PDF export, push/email notifications, and the schema-backed fields listed above.
- Historical analytics and live operational metrics beyond the aggregates currently exposed by the APIs.

## Technology Stack

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 3.3.5
- **Security**: Spring Security, JWT (jjwt 0.12.6)
- **Database**: MySQL with Spring Data JPA; H2 is used by tests
- **Documentation**: springdoc OpenAPI 2.6.0
- **Media**: Cloudinary SDK integration, disabled by default
- **Build Tool**: Maven

### Frontend
- **Framework**: React 19
- **Build Tool**: Vite 6 (declared as `^6.0.5`)
- **Styling**: Tailwind CSS 3.4.17
- **Routing**: React Router 7.1.1
- **State Management**: React context for authentication/theme; Zustand is a declared dependency but is not used by current feature code
- **Forms**: React Hook Form 7.54.2
- **Animations**: Framer Motion 11.15.0
- **Charts**: Recharts 2.15.0
- **Icons**: Lucide React 0.468.0
- **HTTP Client**: Axios 1.7.9

### Runtime and Tooling
- **Containerization**: Docker and Docker Compose configuration is present; Docker execution has not been verified in this environment
- **Web Server**: Nginx serves the production frontend container

## Project Structure

```
Career-Nest/
├── backend/
│   ├── src/main/java/com/careernest/
│   │   ├── common/{dto,exception,security,util}/
│   │   ├── config/
│   │   └── modules/{admin,assistant,audit,auth,certification,coding,
│   │       dashboard,health,internship,learning,notification,profile,
│   │       project,resume,timeline,user}/
│   ├── src/main/resources/{application.yml,application-test.yml,application-prod.yml}
│   ├── src/test/java/com/careernest/modules/{auth,dashboard}/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── app/                  # Router and context providers
│   │   ├── components/           # Shared UI and layouts
│   │   ├── features/             # Per-feature pages and API clients
│   │   ├── lib/                  # Axios and token helpers
│   │   ├── styles/
│   │   └── main.jsx
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── docker-compose.yml
├── .env.example                  # Placeholder values only
└── README.md
```

## Local Development

### Requirements

- Java 21
- Maven 3.9 or later
- Node.js 20 or later and npm
- MySQL 8 for a local MySQL run
- Docker Compose is optional; container execution has not been verified

### Database and Environment

The backend requires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`. Spring Boot does **not** automatically load `backend/.env`; export these variables in the shell that launches Maven. The root `.env.example` is for Docker Compose. Examples contain placeholders only; replace them locally and never commit `.env` files.

The default Hibernate mode is `validate`. An existing database must already contain a schema compatible with the JPA entities, including the `STUDENT`, `ADMIN`, and `SUPER_ADMIN` role records needed by role-dependent flows. This repository does not include Flyway/Liquibase migrations or SQL schema scripts. Do not switch a persistent database to schema-update mode as a substitute for an approved migration.

Startup seeding is disabled by default (`SEED_ROLES=false`, `SEED_ADMIN=false`). Role seeding and optional admin creation are explicit opt-ins. Admin seeding additionally requires `ADMIN_EMAIL` and `ADMIN_PASSWORD`; existing matching accounts are not overwritten. Do not enable seeding casually against a shared database.

For local PowerShell development, set the values in the current shell using your own local credentials and a generated random JWT secret of at least 32 bytes:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'dev'
$env:DB_URL = 'jdbc:mysql://localhost:3306/careernest?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:DB_USERNAME = '<local database user>'
$env:DB_PASSWORD = '<local database password>'
$env:JWT_SECRET = '<generated random secret, at least 32 bytes>'
$env:SERVER_PORT = '8081'
$env:JPA_DDL_AUTO = 'validate'
$env:SEED_ROLES = 'false'
$env:SEED_ADMIN = 'false'
$env:CLOUDINARY_ENABLED = 'false'
```

### Run the Backend

From the repository root, after configuring the environment and provisioning a compatible database schema:

```powershell
mvn -f backend/pom.xml spring-boot:run
```

The backend listens on port `8081` by default. Do not point the `test` profile at MySQL; it uses an isolated in-memory H2 database and `create-drop` for automated tests only.

### Run the Frontend

In a second terminal:

```powershell
cd frontend
npm ci
npm run dev
```

Vite serves the frontend on `http://127.0.0.1:5173` and proxies `/api` to `http://127.0.0.1:8081` by default. Configure `VITE_BACKEND_PROXY_TARGET` if the backend uses another local port. The frontend API base defaults to the same-origin path `/api/v1`.

### Docker Compose

Copy the placeholder template to a local root `.env`, provide local values, and run `docker compose up --build`. Compose expects DB root/user passwords and a JWT secret; it maps the frontend to `${FRONTEND_PORT:-5173}` and the backend to `${SERVER_PORT:-8081}`. The MySQL image initializes an empty database volume, but the application does not create CareerNest tables or seed roles by default. A compatible schema and role rows must be provisioned before authenticated flows are usable. The Docker configuration has not been runtime-tested.

## Runtime Configuration

- `JPA_DDL_AUTO` defaults to `validate`; normal application startup does not update the schema.
- `SEED_ROLES` and `SEED_ADMIN` default to `false`.
- `JWT_SECRET` is required and must be a non-placeholder secret of at least 32 bytes.
- Set `CLOUDINARY_ENABLED=true` and provide all Cloudinary variables to enable image uploads. When disabled, the application starts without Cloudinary and upload requests return HTTP 503.
- Refresh cookies are HttpOnly. Set `REFRESH_COOKIE_SECURE=true` when serving over HTTPS; the `prod` profile defaults it to true.
- `CORS_ALLOWED_ORIGINS` is a comma-separated origin list. Local defaults allow the Vite localhost origins.
- Email delivery is disabled until an `EmailDeliveryProvider` Spring bean is supplied. Verification and password-reset tokens are hashed in storage and are not logged or delivered by the default provider.

## API Documentation

With the backend running, use Swagger UI at `http://localhost:8081/swagger-ui.html` or the OpenAPI document at `http://localhost:8081/v3/api-docs`.

### Endpoint Groups

- **Authentication**: `/api/v1/auth` — signup, login, refresh, logout, logout-all, current user, sessions, email verification, and password recovery.
- **Profile**: `/api/v1/profile` — setup read/update and profile-photo upload/delete.
- **Dashboard**: `GET /api/v1/dashboard/summary` — per-user aggregates and recent/upcoming data.
- **Projects**: `/api/v1/projects` — CRUD, search, status filtering, featured toggle, and screenshot operations under `/screenshots`.
- **Resumes**: `/api/v1/resumes` — CRUD, primary selection, version history, and an export endpoint that currently returns resume data rather than a PDF file.
- **Certifications**: `/api/v1/certifications` — CRUD, search, category filtering, and count.
- **Learning**: `/api/v1/learning/courses` — CRUD, search, status filtering, and count.
- **Internships**: `/api/v1/internships` — CRUD, search, status filtering, and count.
- **Coding profiles**: `/api/v1/coding-profiles` — manual profile CRUD, search, and platform filtering.
- **Timeline**: `/api/v1/timeline` — CRUD, search, and type filtering.
- **Notifications**: `/api/v1/notifications` — list/unread/count, mark read/read-all, and delete.
- **Career health**: `GET /api/v1/health-score`; public liveness response: `GET /api/v1/health`.
- **Assistant**: `POST /api/v1/assistant` — rule-based responses; no external AI provider is connected.
- **Admin**: `/api/v1/admin` — overview, users, and audit-log GET endpoints. User mutation endpoints are not implemented.

The API uses a common response envelope. Protected routes require a bearer access token. Admin routes require `ADMIN` or `SUPER_ADMIN`; `/api/v1/super-admin/**` is restricted to `SUPER_ADMIN`.

## Architecture

The backend uses feature packages under `com.careernest.modules`, with controllers, services, repositories, DTOs, and JPA entities. Shared DTOs, exception handling, security, and utilities live under `common`; application beans live under `config`.

The frontend is feature-oriented under `frontend/src/features`, with shared routing/providers under `app`, shared layouts/components under `components`, and Axios/token utilities under `lib`. API calls use the shared Axios client. React context provides authentication and theme state.

Hibernate is configured with `ddl-auto: validate` by default. There are no checked-in Flyway/Liquibase migrations or SQL initialization scripts. A compatible database schema must be provisioned before startup; the application does not create or update it automatically. Startup role/admin seeding is disabled by default.

## Security Notes

- Passwords use BCrypt strength 12.
- Access tokens are signed JWTs with configured expiry; refresh tokens are hash-stored and rotated.
- Browser refresh tokens use an HttpOnly cookie; access tokens remain in frontend memory.
- New accounts stay pending until email verification succeeds.
- Verification/reset tokens are hashed in storage and are not logged.
- Email delivery is not configured in the default repository state.
- Ownership checks restrict student records to the authenticated user.
- CORS origins, database credentials, JWT secret, and optional Cloudinary credentials are supplied through environment variables.

## Testing and Build

Backend tests use the isolated H2 `test` profile:

```powershell
mvn -f backend/pom.xml test
```

Build the backend package with:

```powershell
mvn -f backend/pom.xml package
```

Build the frontend with:

```powershell
cd frontend
npm run build
```

`package.json` does not define an `npm test` script. The configured `npm run lint` command currently lacks an ESLint 9 configuration file and is not a passing quality gate. The production frontend build currently emits a large-chunk advisory.

### Email and Cloudinary Configuration

Email delivery is intentionally disabled unless an `EmailDeliveryProvider` implementation is configured. Verification and recovery tokens are hashed in storage and are not written to logs. Until a provider is supplied, signup remains pending and the API response states that email delivery is unavailable; verification/reset emails are not delivered.

Cloudinary is optional for application startup. Set `CLOUDINARY_ENABLED=true` and supply all Cloudinary variables to enable uploads; otherwise upload requests return HTTP 503. Set `REFRESH_COOKIE_SECURE=true` behind HTTPS (it defaults to true in the `prod` profile).

The backend binds to port 8081. In local development, Vite proxies `/api` to `VITE_BACKEND_PROXY_TARGET` (default `http://127.0.0.1:8081`). In Compose, Nginx serves the frontend on port 80 and proxies `/api` to the backend service on port 8081. `VITE_API_BASE_URL` should remain `/api/v1` for the same-origin deployment.

## Runtime Limitations

- The current source has not been declared production-ready. Docker Compose execution and production hardening have not been verified.
- The database schema must be provisioned and validated independently; this repository does not ship migrations or schema SQL.
- Email delivery and Cloudinary require external configuration. The default email provider intentionally sends nothing.
- GitHub/coding-platform synchronization and AI model integration are not implemented.
- Resume PDF generation is not implemented. The resume preview can be printed or saved to PDF through the browser's print dialog.
- Profile skills/interests and several additional course, certification, internship, and coding-practice fields require approved schema work before they can be persisted.
