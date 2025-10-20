# Luthen Authentication Service

## Overview
Luthen is a reusable authentication service built with Java, Dropwizard, and PostgreSQL. It provides signup, login, session management with JWT tokens, role-based access control (RBAC), and permission management.

## Project Architecture

### Multi-Module Maven Structure
```
luthen/
├── api/         - API models, requests, and responses
├── bundle/      - Authentication bundle for integration
├── service/     - Main Dropwizard service application
└── pom.xml      - Parent POM
```

### Key Technologies
- **Framework**: Dropwizard 4.0.1 (Jersey, Jetty, Jackson, Hibernate)
- **Database**: PostgreSQL with Hibernate ORM
- **Security**: JWT (jjwt), BCrypt password hashing
- **API Documentation**: Swagger/OpenAPI
- **Build**: Maven 3.8.6
- **Java**: JVM 23 (OpenJDK 23)

### Database Schema
- `users` - User accounts with email, username, password hash
- `roles` - Role definitions
- `permissions` - Permission definitions
- `user_roles` - Many-to-many relationship between users and roles
- `role_permissions` - Many-to-many relationship between roles and permissions

## API Endpoints

### Authentication (`/auth`)
- `POST /auth/signup` - Create new user account
- `POST /auth/login` - User login with JWT token
- `POST /auth/create/role` - Create a new role
- `POST /auth/create/permission` - Create a new permission
- `POST /auth/test` - Test authentication
- `POST /auth/test/decrypt` - Test token decryption

### Client Management (`/client`)
- `POST /client/refresh` - Refresh client tokens

### Health & Monitoring
- `GET /healthcheck` - Application health check
- `GET /swagger` - Swagger API documentation
- `GET /openapi.{json|yaml}` - OpenAPI specification

### Admin Console
Available on port 5001:
- `POST /tasks/log-level` - Adjust logging levels
- `POST /tasks/gc` - Trigger garbage collection

## Environment Configuration

### Database Variables (Auto-configured from Replit PostgreSQL)
- `DB_USERNAME` - Database user (mapped from PGUSER)
- `DB_PASSWORD` - Database password (mapped from PGPASSWORD)
- `DB_HOST` - Database host (mapped from PGHOST)
- `DB_PORT` - Database port (mapped from PGPORT)
- `DB_NAME` - Database name (mapped from PGDATABASE)

### Security Variables (Auto-generated)
- `RSA_PRIVATE_KEY` - Private key for JWT signing (Base64 encoded DER format)
- `RSA_PUBLIC_KEY` - Public key for JWT verification (Base64 encoded DER format)
- `CLIENT_FILTER_SEED` - Seed for client filtering

### Application Variables
- `MOVITRAK_CLIENT_ID` - Example client ID
- `MOVITRAK_CLIENT_SECRET` - Example client secret

## Development

### Building the Project
```bash
# Build all modules
mvn clean install -DskipTests

# Build service module only
mvn -pl service clean package -DskipTests
```

### Running Locally
The application starts automatically via the configured workflow. The start script is located at `/tmp/start_server.sh`.

Manual start:
```bash
java -jar service/target/service-1.0.0.jar server service/config/local.yaml
```

### Configuration Files
- `service/config/local.yaml` - Main configuration file
- `service/config/tables_postgres.sql` - Database schema

## Deployment
Deployment is configured for VM (always running) deployment target:
- Build command: `mvn clean package -DskipTests`
- Run command: Starts the Dropwizard server with all environment variables configured
- The server maintains state in server memory and requires continuous operation

## Known Issues & Notes

### WARN Messages (Non-critical)
The following warnings appear in logs but don't affect functionality:
- Services (UserStore, AuthService, LuthenTokenService, LuthenClientService) are registered as providers but don't implement provider interfaces - this is expected for Dropwizard dependency injection

### Java Version Compatibility
- Project originally designed for Java 23
- Successfully downgraded to Java 19 for Replit compatibility
- Removed preview features (`--enable-preview` flag)
- Changed `List.getFirst()` to `List.get(0)` for Java 19 compatibility

### RSA Key Generation
- RSA keys are generated once during initial setup using OpenSSL
- Keys are stored in `/tmp/` directory and loaded at runtime
- For production deployment, keys should be securely stored and managed

## User Preferences
None specified yet.

## Project Status
✅ Fully functional and running
✅ Database schema initialized
✅ All dependencies installed
✅ Development server running on port 5000
✅ Deployment configured for VM target
