# Book Review Platform

A peer-to-peer book review service built with Java 17, Spring Boot 3.x, Hibernate (JPA), PostgreSQL, and secured with Spring Security (JWT).

## Features
- User registration, login (JWT), and roles (USER, MODERATOR)
- Book catalog CRUD, search/filter by author/genre, pagination
- Review CRUD, only authenticated users can create/edit, only moderators can delete
- Dockerized for easy deployment
- Swagger UI for API documentation

## Getting Started

### Prerequisites
- Docker & Docker Compose

### Quick Start (Docker Compose)
1. Clone the repository
2. Set your DB username and password in `docker-compose.yml` and `src/main/resources/application.properties`
3. Run:
   ```sh
   docker-compose up --build
   ```
4. Access the API at [http://localhost:8080](http://localhost:8080)
5. Access Swagger UI at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Manual Setup
1. Install PostgreSQL and create a database/user matching your `application.properties`
2. Build and run the app:
   ```sh
   ./mvnw clean package
   java -jar target/BookReview-0.0.1-SNAPSHOT.jar
   ```

## API Documentation
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

## Testing
- Run tests with:
  ```sh
  ./mvnw test
  ```

## Continuous Integration
- GitHub Actions workflow in `.github/workflows/ci.yml` runs tests on every push/PR.

## API Testing with Postman

You can test all API endpoints using the following Postman collection:

[BookReview Postman Collection](https://orange-comet-916343.postman.co/workspace/Team-Workspace~da6e91e8-51a6-4712-82ab-71b6cc2ac04e/collection/27047894-90ddc9a5-4625-4be3-93fb-7ab9e0d42878?action=share&creator=27047894)


## ER Diagram
See [docs/ERD.md](docs/ERD.md)

# Book Review Platform - Data Schema & Design Documentation

## Entity Relationship Diagram

**Legend:**
- **PK**: Primary Key
- **FK**: Foreign Key
- **UK**: Unique Constraint
- `[]`: Array/Set
- 1/∞: One/Many
- 1/∞: One/Many

```
┌─────────────────────┐         ┌─────────────────────┐
│        USER         │         │        BOOK         │
├─────────────────────┤         ├─────────────────────┤
│ PK: id              │         │ PK: id              │
│ UK: username        │    1    │ UK: isbn            │
│ UK: email           │ ─────── │ FK: author_id       │
│    password         │    ∞    │    title            │
│    first_name       │         │    subtitle         │
│    last_name        │         │    description      │
│    roles[]          │         │    publication_date │
│    is_active        │         │    average_rating   │
│    created_at       │         │    review_count     │
└─────────────────────┘         └─────────────────────┘
         │                               │
         │                               │
         │ 1                             │ 1
         │                               │
         │                               │
         ∞                               ∞
┌─────────────────────┐         ┌─────────────────────┐
│       REVIEW        │         │    BOOK_GENRES      │
├─────────────────────┤         ├─────────────────────┤
│ PK: id              │         │ FK: book_id         │
│ FK: user_id         │         │ FK: genre_id        │
│ FK: book_id         │         └─────────────────────┘
│ UK: user_id+book_id │                   │
│    content          │                   │
│    rating           │                   ∞
│    title            │         ┌─────────────────────┐
│    helpful_count    │         │       GENRE         │
│    created_at       │         ├─────────────────────┤
└─────────────────────┘         │ PK: id              │
                                │ UK: name            │
                                │ UK: slug            │
                                │    description      │
                                │    is_active        │
                                └─────────────────────┘

┌─────────────────────┐
│  BOOK_CO_AUTHORS    │
├─────────────────────┤
│ FK: book_id         │
│ FK: author_id       │
└─────────────────────┘
```

## ER Diagram
See [docs/ERD.md](docs/ERD.md)

## License
MIT 