# Student CRUD Application

A comprehensive Spring Boot application that provides RESTful API endpoints for managing Student entities with full CRUD operations, validation, and comprehensive testing.

## Features

- **Full CRUD Operations**: Create, Read, Update, Delete students
- **Data Validation**: Bean validation with custom error messages
- **Database Integration**: JPA/Hibernate with H2 in-memory database
- **RESTful API**: REST endpoints with proper HTTP status codes
- **Exception Handling**: Global exception handler with standardized error responses
- **Comprehensive Testing**: Unit tests with Mockito and integration tests
- **Search & Filter**: Advanced querying capabilities

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database**
- **Lombok**
- **JUnit 5**
- **Mockito**
- **Maven**

## Project Structure

```
src/
├── main/
│   ├── java/com/example/student/
│   │   ├── StudentCrudApplication.java
│   │   ├── controller/
│   │   │   ├── StudentController.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ErrorResponse.java
│   │   ├── model/
│   │   │   └── Student.java
│   │   ├── repository/
│   │   │   └── StudentRepository.java
│   │   ├── service/
│   │   │   └── StudentService.java
│   │   └── exception/
│   │       ├── StudentNotFoundException.java
│   │       └── DuplicateEmailException.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/example/student/
    │   ├── StudentCrudApplicationIntegrationTest.java
    │   ├── controller/
    │   │   └── StudentControllerTest.java
    │   ├── service/
    │   │   └── StudentServiceTest.java
    │   └── repository/
    │       └── StudentRepositoryIntegrationTest.java
    └── resources/
        └── application-test.properties
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd student-crud
   ```

2. **Build the project**

   ```bash
   mvn clean install
   ```

3. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

   The application will start on `http://localhost:8080`

4. **Access H2 Console** (optional)
   - URL: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:studentdb`
   - Username: `sa`
   - Password: (leave empty)

### Running Tests

```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest=*Test

# Run only integration tests
mvn test -Dtest=*IntegrationTest
```

## API Endpoints

### Student Management

| Method   | Endpoint                      | Description          |
| -------- | ----------------------------- | -------------------- |
| `POST`   | `/api/students`               | Create a new student |
| `GET`    | `/api/students`               | Get all students     |
| `GET`    | `/api/students/{id}`          | Get student by ID    |
| `GET`    | `/api/students/email/{email}` | Get student by email |
| `PUT`    | `/api/students/{id}`          | Update student       |
| `DELETE` | `/api/students/{id}`          | Delete student       |

### Search & Filter

| Method | Endpoint                                                  | Description                           |
| ------ | --------------------------------------------------------- | ------------------------------------- |
| `GET`  | `/api/students/search?name={name}`                        | Search students by name               |
| `GET`  | `/api/students/age-range?minAge={min}&maxAge={max}`       | Get students by age range             |
| `GET`  | `/api/students/older-than?minAge={age}`                   | Get students older than specified age |
| `GET`  | `/api/students/count/age-range?minAge={min}&maxAge={max}` | Count students in age range           |

## Student Model

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 25,
  "address": "123 Main St",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### Validation Rules

- **name**: Required, 2-100 characters
- **email**: Required, valid email format, unique, max 150 characters
- **age**: Required, between 1-150
- **address**: Optional, max 500 characters

## Example API Usage

### Create a Student

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "age": 22,
    "address": "456 Oak Ave"
  }'
```

### Get All Students

```bash
curl http://localhost:8080/api/students
```

### Update a Student

```bash
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith Updated",
    "email": "jane.updated@example.com",
    "age": 23,
    "address": "789 Pine Rd"
  }'
```

### Delete a Student

```bash
curl -X DELETE http://localhost:8080/api/students/1
```

## Testing

### Unit Tests

- **StudentServiceTest**: Tests business logic with mocked repository
- **StudentControllerTest**: Tests REST endpoints with mocked service

### Integration Tests

- **StudentRepositoryIntegrationTest**: Tests database operations with H2
- **StudentCrudApplicationIntegrationTest**: Tests full application flow

### Test Coverage

The application includes comprehensive test coverage for:

- CRUD operations
- Validation scenarios
- Error handling
- Edge cases
- Database operations

## Error Handling

The application provides standardized error responses:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "name": "Name is required",
    "email": "Email should be valid"
  }
}
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License.
