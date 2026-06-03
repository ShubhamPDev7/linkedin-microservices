# LinkedIn Microservices

A backend system built with Java 21 and Spring Boot, modelling core LinkedIn features — user auth, posts, likes, and social graph connections — across independent microservices.

---

## Architecture

```
Client
  └── API Gateway (port 8080)
        ├── /api/v1/users/**       → UserService
        ├── /api/v1/posts/**       → PostsService
        └── /api/v1/connections/** → ConnectionsService

DiscoverServer (Eureka, port 8761)
  └── All services register here for discovery & load balancing
```

---

## Services

### DiscoverServer
Eureka service registry. Must be started first — all other services register with it on startup.

### APIGateway
Reactive gateway (Spring Cloud Gateway WebFlux) that routes incoming requests to the appropriate downstream service using client-side load balancing.

### UserService
Handles user registration and authentication.
- **Database:** PostgreSQL
- **Auth:** JWT (jjwt 0.12.6) + BCrypt password hashing

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/signup` | Register a new user |
| POST | `/auth/login` | Login and receive a JWT token |

### PostsService
Manages posts and likes.
- **Database:** PostgreSQL

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/core` | Create a new post |
| GET | `/core/{postId}` | Get a post by ID |
| GET | `/core/users/{userId}/allPosts` | Get all posts by a user |
| POST | `/likes/{postId}` | Like a post |
| DELETE | `/likes/{postId}` | Unlike a post |

### ConnectionsService
Manages the social graph using a Neo4j graph database. `Person` nodes are connected via `CONNECTED_TO` relationships, enabling efficient graph traversal queries.

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/core/{userId}/first-degree` | Get first-degree connections of a user |

---

## Tech Stack

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway (WebFlux) |
| User & Posts DB | PostgreSQL (Spring Data JPA / Hibernate) |
| Connections DB | Neo4j (Spring Data Neo4j) |
| Authentication | JWT (jjwt 0.12.6) + BCrypt (jbcrypt 0.4) |
| Object Mapping | ModelMapper 3.2.0 |
| Boilerplate | Lombok |
| Build Tool | Maven |

---

## Getting Started

### Prerequisites
- Java 21
- Maven
- PostgreSQL (for UserService and PostsService)
- Neo4j (for ConnectionsService)

### Configuration

Each service has an `application.properties.example` or `application.yml.example` in its `src/main/resources/` directory. Copy and rename it, then fill in your environment values:

```bash
# Example for UserService
cp userService/src/main/resources/application.properties.example \
   userService/src/main/resources/application.properties
```

Key values to configure:

| Service | Config Key | Description |
|---|---|---|
| UserService | `spring.datasource.url` | PostgreSQL connection URL |
| UserService | `spring.datasource.username` / `password` | DB credentials |
| UserService | `jwt.secretKey` | Secret key for signing JWTs |
| PostsService | `spring.datasource.*` | PostgreSQL connection |
| ConnectionsService | `spring.neo4j.uri` | Neo4j bolt URI |
| ConnectionsService | `spring.neo4j.authentication.*` | Neo4j credentials |
| All services | `eureka.client.service-url.defaultZone` | Eureka server URL |

### Running the Services

Start services in this order:

```bash
# 1. Eureka Discovery Server
cd DiscoverServer && ./mvnw spring-boot:run

# 2. API Gateway
cd APIGateway && ./mvnw spring-boot:run

# 3. Application services (order doesn't matter)
cd userService && ./mvnw spring-boot:run
cd postsService && ./mvnw spring-boot:run
cd ConnectionsService && ./mvnw spring-boot:run
```

All services will auto-register with Eureka. You can verify at `http://localhost:8761`.

---

## Project Structure

```
linkedin-microservices-main/
├── DiscoverServer/          # Eureka service registry
├── APIGateway/              # Spring Cloud Gateway
├── userService/             # Auth: signup, login, JWT
├── postsService/            # Posts and likes
└── ConnectionsService/      # Social graph (Neo4j)
```

---

## Known Limitations / Work in Progress

- **User ID not extracted from JWT** — Services currently hardcode `userId = 1L`. The `JwtService.getUserIdFromToken()` method is implemented but commented out; it needs to be wired into controllers once JWT propagation via the gateway is in place.
- **No auth filter at the gateway** — The API Gateway does not yet validate JWT tokens on incoming requests.
- **No connection management endpoints** — `ConnectionsService` supports querying connections but has no API yet for creating or removing them.
- **Notifications not implemented** — Post like events have a `TODO` placeholder for notifying the post owner.

---

## Author

[shubhampdev7](https://github.com/shubhampdev7)