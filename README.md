# Student Management API

A simple Spring Boot CRUD REST API for managing students. Built to learn CI/CD with GitHub Actions.

## Tech Stack

- Java 21, Spring Boot 3.3.6
- PostgreSQL (runtime), H2 (tests)
- Lombok, Bean Validation
- Maven, Docker
- GitHub Actions CI

## API Endpoints

| Method | Endpoint              | Description          |
|--------|----------------------|----------------------|
| GET    | `/api/students`      | List all students    |
| GET    | `/api/students/{id}` | Get student by ID    |
| POST   | `/api/students`      | Create a student     |
| PUT    | `/api/students/{id}` | Update a student     |
| DELETE | `/api/students/{id}` | Delete a student     |

### Sample Request Body

```json
{
  "firstName": "Sharath",
  "lastName": "Kumar",
  "email": "sharath@example.com",
  "department": "CSE"
}
```

## Local Setup

1. **Start PostgreSQL** and create a database:
   ```sql
   CREATE DATABASE studentdb;
   ```

2. **Run the app:**
   ```bash
   mvn spring-boot:run
   ```
   The API starts on `http://localhost:9043`.

3. **Run tests** (uses H2, no PostgreSQL needed):
   ```bash
   mvn clean verify
   ```

## Docker

```bash
docker build -t student-management .
docker run -p 9043:9043 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/studentdb \
  student-management
```

## CI/CD

Every push to `main` triggers the GitHub Actions workflow (`.github/workflows/ci.yml`) which checks out, sets up Java 21, and runs `mvn -B clean verify`.
