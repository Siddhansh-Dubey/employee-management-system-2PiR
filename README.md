# Employee Management System

A RESTful Employee Management System built with **Spring Boot 4.1.1** and **Java 21** as a technical assessment for Twopir Consulting.

## Technologies

| Technology          | Version |
|---------------------|---------|
| Java                | 21      |
| Spring Boot         | 4.1.1   |
| Spring Web MVC      | 7.x     |
| Spring Data JPA     | 4.x     |
| Jakarta Validation  | 4.x     |
| H2 Database         | 2.x     |
| JUnit 5             | 5.x     |
| Maven Wrapper       | 3.x     |

## Project Structure

```
src/main/java/com/twopir/employeemanagement/
├── EmployeeManagementSystemApplication.java   # Application entry point
├── controller/
│   └── EmployeeController.java                # REST API endpoints
├── service/
│   └── EmployeeService.java                   # Business logic
├── repository/
│   └── EmployeeRepository.java                # Data access layer
├── entity/
│   ├── Employee.java                          # JPA entity
│   └── EmployeeStatus.java                    # ACTIVE/INACTIVE enum
├── dto/
│   ├── EmployeeRequest.java                   # Input DTO with validation
│   └── EmployeeResponse.java                  # Output DTO
└── exception/
    ├── EmployeeNotFoundException.java          # Custom 404 exception
    ├── ErrorResponse.java                      # Structured error body
    └── GlobalExceptionHandler.java             # Global @RestControllerAdvice
```

## How to Run the Application

```bash
# Clone the repository
git clone <repository-url>
cd employee-management-system-2PiR

# Run the application
./mvnw spring-boot:run

# The application starts at http://localhost:8080
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## How to Run Tests

```bash
./mvnw clean test
```

## API Endpoints

| Method | Endpoint                   | Description              | Status Code |
|--------|----------------------------|--------------------------|-------------|
| POST   | `/api/employees`           | Create a new employee    | 201 Created |
| GET    | `/api/employees`           | Get all employees (paginated) | 200 OK |
| GET    | `/api/employees/{id}`      | Get employee by ID       | 200 OK      |
| PUT    | `/api/employees/{id}`      | Update an employee       | 200 OK      |
| DELETE | `/api/employees/{id}`      | Delete an employee       | 204 No Content |
| GET    | `/api/employees/search?name=` | Search by name        | 200 OK      |

### Pagination Parameters (GET /api/employees)

| Parameter | Default | Description          |
|-----------|---------|----------------------|
| `page`    | 0       | Page number (0-based)|
| `size`    | 10      | Page size            |
| `sortBy`  | id      | Sort field           |
| `sortDir` | asc     | Sort direction (asc/desc) |

### Search Parameters (GET /api/employees/search)

| Parameter | Required | Description                         |
|-----------|----------|-------------------------------------|
| `name`    | Yes      | Partial name match (case-insensitive)|
| `page`    | No       | Page number (default: 0)            |
| `size`    | No       | Page size (default: 10)             |

## Example Request/Response

### Create Employee

**Request:**
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "department": "Engineering",
    "salary": 75000.00,
    "status": "ACTIVE"
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "salary": 75000.00,
  "status": "ACTIVE"
}
```

### Get Employee by ID

```bash
curl http://localhost:8080/api/employees/1
```

### Get All Employees (Paginated)

```bash
curl "http://localhost:8080/api/employees?page=0&size=5&sortBy=name&sortDir=asc"
```

### Update Employee

```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "department": "Marketing",
    "salary": 85000.00,
    "status": "INACTIVE"
  }'
```

### Delete Employee

```bash
curl -X DELETE http://localhost:8080/api/employees/1
```

### Search by Name

```bash
curl "http://localhost:8080/api/employees/search?name=John"
```

### Validation Error Example

**Request (blank name):**
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "email": "invalid",
    "department": "",
    "salary": -100,
    "status": "ACTIVE"
  }'
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields have invalid values",
  "timestamp": "2026-09-23T14:30:00",
  "fieldErrors": {
    "name": "Name must not be blank",
    "email": "Email must be a valid email address",
    "department": "Department must not be blank",
    "salary": "Salary must be zero or positive"
  }
}
```

### Employee Not Found Example

```bash
curl http://localhost:8080/api/employees/999
```

**Response (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 999",
  "timestamp": "2026-09-23T14:30:00"
}
```

## H2 Database Console

The H2 in-memory database console is available during development:

| Property     | Value                        |
|--------------|------------------------------|
| Console URL  | http://localhost:8080/h2-console |
| JDBC URL     | `jdbc:h2:mem:employeedb`     |
| Username     | `sa`                         |
| Password     | *(empty)*                    |
| Driver Class | `org.h2.Driver`              |

> **Note:** The H2 database is in-memory, so all data is lost when the application restarts.

## Employee Fields

| Field      | Type       | Validation                    |
|------------|------------|-------------------------------|
| id         | Long       | Auto-generated                |
| name       | String     | Must not be blank             |
| email      | String     | Must be a valid email, not blank |
| department | String     | Must not be blank             |
| salary     | BigDecimal | Must be zero or positive      |
| status     | Enum       | ACTIVE or INACTIVE            |