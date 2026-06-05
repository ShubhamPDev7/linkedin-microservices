<div align="center">

# 🔗 LinkedIn Microservices

**A backend system modelling core LinkedIn features — auth, posts, likes, and social graph connections — across independent microservices.**

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-Eureka-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-User%20%26%20Posts-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Neo4j](https://img.shields.io/badge/Neo4j-Connections-008CC1?style=for-the-badge&logo=neo4j&logoColor=white)](https://neo4j.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)](https://jwt.io/)

*Built to explore microservices patterns, graph databases, and JWT-based authentication in a real-world domain.*

</div>

---

## 📐 Architecture Overview

```
  Client
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│                    API Gateway  :8080                   │
│              (Spring Cloud Gateway / WebFlux)           │
│                                                         │
│  /api/v1/users/**        ──►  UserService               │
│  /api/v1/posts/**        ──►  PostsService              │
│  /api/v1/connections/**  ──►  ConnectionsService        │
└───────────┬─────────────────────────────────────────────┘
            │  routes via client-side load balancing
            ▼
┌───────────────────────────────────────────────────────┐
│              Discovery Server  :8761                  │
│                 (Netflix Eureka)                      │
│                                                       │
│   UserService ──► registered                         │
│   PostsService ──► registered                        │
│   ConnectionsService ──► registered                  │
└───────────────────────────────────────────────────────┘

┌─────────────┐    ┌─────────────┐    ┌──────────────────┐
│ UserService │    │PostsService │    │ConnectionsService│
│   :user     │    │  :posts     │    │   :connections   │
│             │    │             │    │                  │
│  JWT + BCrypt    │ Posts+Likes │    │  Social Graph    │
└──────┬──────┘    └──────┬──────┘    └────────┬─────────┘
       │                  │                    │
  ┌────▼────┐        ┌────▼────┐          ┌────▼────┐
  │ PG DB   │        │ PG DB   │          │  Neo4j  │
  └─────────┘        └─────────┘          └─────────┘
```

---

## 🏗️ Services

| Service | Port | Description |
|---|---|---|
| 🌐 **API Gateway** | `8080` | Single entry point; reactive routing + client-side load balancing |
| 🔍 **Discovery Server** | `8761` | Netflix Eureka — all services self-register here |
| 👤 **User Service** | — | Registration, login, JWT auth + BCrypt password hashing |
| 📝 **Posts Service** | — | Post creation, retrieval, likes and unlikes |
| 🕸️ **Connections Service** | — | Social graph traversal via Neo4j (`CONNECTED_TO` relationships) |

---

## 📡 API Reference

### 👤 User Service — `/api/v1/users`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/signup` | Register a new user |
| `POST` | `/auth/login` | Login and receive a JWT token |

### 📝 Posts Service — `/api/v1/posts`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/core` | Create a new post |
| `GET` | `/core/{postId}` | Get a post by ID |
| `GET` | `/core/users/{userId}/allPosts` | Get all posts by a user |
| `POST` | `/likes/{postId}` | Like a post |
| `DELETE` | `/likes/{postId}` | Unlike a post |

### 🕸️ Connections Service — `/api/v1/connections`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/core/{userId}/first-degree` | Get first-degree connections of a user |

---

## 💻 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Gateway | Spring Cloud Gateway (Reactive / WebFlux) |
| Service Discovery | Spring Cloud Netflix Eureka |
| User & Posts DB | PostgreSQL — Spring Data JPA / Hibernate |
| Connections DB | Neo4j — Spring Data Neo4j |
| Authentication | JWT (jjwt 0.12.6) + BCrypt (jbcrypt 0.4) |
| Object Mapping | ModelMapper 3.2.0 |
| Build | Apache Maven |
| Utilities | Lombok |

---

## 📁 Project Structure

```
linkedin-microservices/
├── DiscoverServer/          ← Eureka service registry        :8761
├── APIGateway/              ← Spring Cloud Gateway           :8080
├── userService/             ← Auth: signup, login, JWT
├── postsService/            ← Posts and likes
└── ConnectionsService/      ← Social graph (Neo4j)
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL (for UserService and PostsService)
- Neo4j (for ConnectionsService)

### Configuration

Each service has an `.example` config file in `src/main/resources/`. Copy and fill in your values:

```bash
# UserService
cp userService/src/main/resources/application.properties.example \
   userService/src/main/resources/application.properties

# PostsService
cp postsService/src/main/resources/application.properties.example \
   postsService/src/main/resources/application.properties

# ConnectionsService
cp ConnectionsService/src/main/resources/application.yml.example \
   ConnectionsService/src/main/resources/application.yml
```

**Key values to configure:**

| Service | Key | Description |
|---|---|---|
| UserService | `spring.datasource.url` | PostgreSQL connection URL |
| UserService | `spring.datasource.username` / `password` | DB credentials |
| UserService | `jwt.secretKey` | Secret key for signing JWTs |
| PostsService | `spring.datasource.*` | PostgreSQL connection |
| ConnectionsService | `spring.neo4j.uri` | Neo4j bolt URI |
| ConnectionsService | `spring.neo4j.authentication.*` | Neo4j credentials |
| All services | `eureka.client.service-url.defaultZone` | Eureka server URL |

### Boot Order

```bash
# 1. Start Eureka first — all other services depend on it
cd DiscoverServer && ./mvnw spring-boot:run

# 2. Start the API Gateway
cd APIGateway && ./mvnw spring-boot:run

# 3. Start application services (any order)
cd userService && ./mvnw spring-boot:run
cd postsService && ./mvnw spring-boot:run
cd ConnectionsService && ./mvnw spring-boot:run
```

Verify all services registered at: **`http://localhost:8761`**

---

## 🚧 Known Limitations

This project is actively in progress. Current gaps:

| Area | Status | Detail |
|---|---|---|
| JWT Propagation | ⏳ Pending | `getUserIdFromToken()` implemented but commented out; controllers hardcode `userId = 1L` |
| Gateway Auth Filter | ⏳ Pending | API Gateway does not yet validate JWTs on incoming requests |
| Connection Management | ⏳ Pending | No endpoints yet to create or remove connections — query only |
| Like Notifications | ⏳ Pending | `TODO` placeholder exists for notifying post owners on likes |

---

## 🗺️ Graph Data Model

ConnectionsService uses **Neo4j** to model the social graph:

```
(:Person {userId}) -[:CONNECTED_TO]-> (:Person {userId})
```

This enables efficient graph traversal queries — first-degree connections, mutual friends, and shortest paths — that would be costly in a relational model.

---

<div align="center">

Built by [shubhampdev7](https://github.com/shubhampdev7) as part of a backend engineering deep-dive into Java, Spring Boot, and Microservices Architecture.

</div>