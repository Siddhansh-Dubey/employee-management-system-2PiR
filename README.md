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
git clone https://github.com/Siddhansh-Dubey/employee-management-system-2PiR
cd employee-management-system-2PiR

# Run the application
./mvnw spring-boot:run

# The application starts at http://localhost:8080
```

On Windows:
```cmd
.\mvnw.cmd spring-boot:run
```

## How to Run Tests

On Unix/macOS:
```bash
./mvnw clean test
```

On Windows:
```cmd
.\mvnw.cmd clean test
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

| Parameter | Default | Description | Validation |
|-----------|---------|-------------|------------|
| `page`    | 0       | Page number | Must be >= 0 |
| `size`    | 10      | Page size   | Must be between 1 and 100 |
| `sortBy`  | id      | Sort field  | Must be one of: `id`, `name`, `email`, `department`, `salary`, `status` |
| `sortDir` | asc     | Direction   | Must be `asc` or `desc` |

*Note: Invalid pagination or sorting parameters will return a `400 Bad Request`.*

### Search Parameters (GET /api/employees/search)

| Parameter | Required | Description | Validation |
|-----------|----------|-------------|------------|
| `name`    | Yes      | Name match  | Cannot be blank or whitespace-only |
| `page`    | No       | Page number | Must be >= 0 (default: 0) |
| `size`    | No       | Page size   | Must be between 1 and 100 (default: 10) |

*Note: Blank search names will return a `400 Bad Request`.*

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

### Duplicate Email Example

If you attempt to create or update an employee with an email that is already in use by another employee:

**Response (409 Conflict):**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Employee with email already exists: john.doe@example.com",
  "timestamp": "2026-09-23T14:30:00"
}
```
*Note: During an update, an employee can retain their own existing email without triggering this error.*

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