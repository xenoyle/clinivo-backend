# Clinivo — Backend

A Spring Boot REST API powering the Clinivo healthcare messaging platform. Provides user authentication, conversation management, real-time messaging via WebSockets, and AES-256 message encryption.

---

## Overview

Clinivo's backend serves as the core data and communication layer for the Clinivo frontend. It handles role-based user management (patients and healthcare providers), manages one-to-one conversations, persists encrypted messages, and broadcasts real-time updates to connected clients over STOMP/WebSocket.

---

## Features

- **Role-based user management** — Separate `PATIENT` and `DOCTOR` (provider) roles with dedicated API paths
- **Conversation management** — Automatically creates or retrieves conversations between a patient and their assigned provider
- **Real-time messaging** — STOMP over WebSocket (SockJS fallback) for live message delivery
- **AES-256 message encryption** — All messages are encrypted at rest; decrypted only on retrieval
- **BCrypt password hashing** — Passwords are never stored in plain text
- **Read receipts** — Tracks unread message counts per user
- **Global exception handling** — Consistent error responses across all endpoints
- **Seed data on startup** — Default doctor and patient accounts are created automatically if none exist

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3 |
| Language | Java 17 |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL (configurable) |
| Real-time | STOMP over WebSocket (SockJS) |
| Security | Spring Security + BCrypt |
| Encryption | AES-256 |
| Testing | JUnit 5 + Mockito |

---

## Project Structure

```
clinivo-backend/src/main/java/.../clinivo_backend/
├── config/
│   ├── CorsConfig.java          # CORS policy (localhost + Netlify)
│   ├── SecurityConfig.java      # Spring Security config + BCrypt bean
│   └── WebSocketConfig.java     # STOMP broker and endpoint configuration
├── controller/
│   ├── ChatWebSocketController.java   # Handles /app/sendMessage over STOMP
│   ├── ConversationController.java    # REST endpoints for conversations
│   ├── MessageController.java         # REST endpoints for messages
│   └── UserController.java            # REST endpoints for users
├── dto/
│   ├── ChatMessage.java         # Incoming WebSocket message payload
│   ├── ConversationResponse.java
│   ├── LoginRequest.java
│   ├── MessageRequest.java
│   ├── MessageResponse.java
│   ├── UserRequest.java
│   └── UserResponse.java
├── exception/
│   └── GlobalExceptionHandler.java   # Centralized error handling
├── model/
│   ├── Conversation.java
│   ├── ConversationParticipant.java
│   ├── Message.java
│   ├── MessageRead.java
│   └── User.java
├── repository/
│   ├── ConversationParticipantRepository.java
│   ├── ConversationRepository.java
│   ├── MessageReadRepository.java
│   ├── MessageRepository.java
│   └── UserRepository.java
├── service/
│   ├── ConversationService.java
│   ├── EncryptionService.java
│   └── UserService.java
│   └── MessageService.java
└── ClinivoBackendApplication.java
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- A running MySQL instance

### Installation

```bash
git clone https://github.com/your-username/clinivo-backend.git
cd clinivo-backend
```

### Configuration

Create or update `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/clinivo
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

# Encryption
# Generate a Base64-encoded 256-bit key and set it here.
# If left blank, the app will auto-generate a key on startup (not suitable for production).
encryption.key=YOUR_BASE64_ENCODED_256_BIT_KEY
```

### Running Locally

```bash
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080`.

### Running Tests

```bash
./mvnw test
```

Tests run under the `test` Spring profile, which skips the seed data `CommandLineRunner`.

---

## Seed Data

On first startup (when no users exist), the app automatically creates two default accounts:

| Role | Email | Password |
|---|---|---|
| Doctor | `doctor@test.com` | `password` |
| Patient | `patient@test.com` | `password` |

---

## API Reference

All REST endpoints are prefixed with `/api`.

### Users — `/api/users`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/users` | Register a new user |
| `POST` | `/api/users/login` | Authenticate and return user info |
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get a user by ID |
| `PUT` | `/api/users/{id}` | Update a user |
| `DELETE` | `/api/users/{id}` | Delete a user |

**Register request body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@example.com",
  "phoneNumber": "8431234567",
  "password": "securePassword",
  "role": "PATIENT"
}
```

### Conversations — `/api/conversations`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/conversations/{doctorId}/{patientId}` | Create or retrieve an existing conversation |
| `GET` | `/api/conversations/doctor/{doctorId}` | Get all conversations for a provider |
| `GET` | `/api/conversations/patient/{patientId}` | Get the conversation for a patient (auto-assigns a doctor if none exists) |

### Messages — `/api/messages`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/messages` | Send a message (REST fallback) |
| `GET` | `/api/messages/conversation/{conversationId}` | Get all messages in a conversation |
| `POST` | `/api/messages/read?conversationId=&userId=` | Mark messages as read |
| `GET` | `/api/messages/unread/{userId}` | Get unread message count for a user |

---

## WebSocket / Real-Time Messaging

The app uses STOMP over WebSocket with a SockJS fallback.

**Connect endpoint:** `ws://localhost:8080/ws`

**Send a message:**
- Destination: `/app/sendMessage`
- Payload:
```json
{
  "conversationId": 1,
  "senderId": 2,
  "content": "Hello, how are you feeling today?"
}
```

**Subscribe to receive messages:**
- `/topic/messages/{userId}` — Receives new messages for a specific user
- `/topic/conversations/{doctorId}` — Notifies a provider when a new conversation is created

---

## Security Notes

- **CSRF** is disabled (stateless API).
- All endpoints are currently open (`permitAll`). JWT or session-based auth is recommended before any production deployment.
- **Message content** is encrypted with AES-256 before being written to the database, and decrypted before being returned to clients.
- Set a stable `encryption.key` in your environment. Without one, the app generates a new key on each restart, making previously encrypted messages unreadable.

---

## CORS

The following origins are permitted by default:

- `http://localhost:5173` (local Vite dev server)
- `https://clinivo-frontend.netlify.app` (production frontend)

Update `CorsConfig.java` to add additional origins as needed.

---

## Testing

The test suite covers controllers, repositories, and service logic. Tests use the `test` Spring profile and Mockito for service-layer mocking.

```
test/
├── controller/
│   ├── ChatWebSocketControllerTest.java
│   ├── ConversationControllerTest.java
│   ├── MessageControllerTest.java
│   └── UserControllerTest.java
├── repository/
│   ├── ConversationRepositoryTest.java
│   ├── MessageRepositoryTest.java
│   └── UserRepositoryTest.java
├── service/
│   └── UserServiceTest.java
└── ClinivoBackendApplicationTests.java
```
