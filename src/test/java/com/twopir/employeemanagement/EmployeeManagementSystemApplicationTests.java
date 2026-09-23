package com.twopir.employeemanagement;

import com.twopir.employeemanagement.dto.EmployeeRequest;
import com.twopir.employeemanagement.entity.EmployeeStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeManagementSystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    // --- Create Employee ---

    @Test
    void createEmployee_withValidData_returns201() throws Exception {
        EmployeeRequest request = new EmployeeRequest(
                "John Doe", "john@example.com", "Engineering",
                new BigDecimal("75000.00"), EmployeeStatus.ACTIVE
        );

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.department", is("Engineering")))
                .andExpect(jsonPath("$.salary", is(75000.00)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void createEmployee_withBlankName_returns400() throws Exception {
        EmployeeRequest request = new EmployeeRequest(
                "", "john@example.com", "Engineering",
                new BigDecimal("75000.00"), EmployeeStatus.ACTIVE
        );

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void createEmployee_withInvalidEmail_returns400() throws Exception {
        EmployeeRequest request = new EmployeeRequest(
                "John Doe", "not-an-email", "Engineering",
                new BigDecimal("75000.00"), EmployeeStatus.ACTIVE
        );

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void createEmployee_withNegativeSalary_returns400() throws Exception {
        EmployeeRequest request = new EmployeeRequest(
                "John Doe", "john@example.com", "Engineering",
                new BigDecimal("-1000.00"), EmployeeStatus.ACTIVE
        );

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.salary").exists());
    }

    @Test
    void createEmployee_withNullStatus_returns400() throws Exception {
        String json = """
                {
                    "name": "John Doe",
                    "email": "john@example.com",
                    "department": "Engineering",
                    "salary": 75000.00,
                    "status": null
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.status").exists());
    }

    // --- Get Employee ---

    @Test
    void getEmployeeById_existing_returns200() throws Exception {
        String id = createSampleEmployee();

        mockMvc.perform(get("/api/employees/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Smith")));
    }

    @Test
    void getEmployeeById_nonExisting_returns404() throws Exception {
        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 999"));
    }

    // --- Get All Employees (with pagination) ---

    @Test
    void getAllEmployees_empty_returnsEmptyPage() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements", is(0)));
    }

    @Test
    void getAllEmployees_withData_returnsPaginatedResults() throws Exception {
        createSampleEmployee();
        createEmployeeWithEmail("john@example.com", "John Doe");

        mockMvc.perform(get("/api/employees")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.totalElements", is(2)))
                .andExpect(jsonPath("$.page.totalPages", is(2)));
    }

    // --- Update Employee ---

    @Test
    void updateEmployee_existing_returns200() throws Exception {
        String id = createSampleEmployee();

        EmployeeRequest updateRequest = new EmployeeRequest(
                "Jane Updated", "jane.updated@example.com", "Marketing",
                new BigDecimal("85000.00"), EmployeeStatus.INACTIVE
        );

        mockMvc.perform(put("/api/employees/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Updated")))
                .andExpect(jsonPath("$.email", is("jane.updated@example.com")))
                .andExpect(jsonPath("$.department", is("Marketing")))
                .andExpect(jsonPath("$.status", is("INACTIVE")));
    }

    @Test
    void updateEmployee_nonExisting_returns404() throws Exception {
        EmployeeRequest updateRequest = new EmployeeRequest(
                "Nobody", "nobody@example.com", "None",
                new BigDecimal("0.00"), EmployeeStatus.ACTIVE
        );

        mockMvc.perform(put("/api/employees/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    // --- Delete Employee ---

    @Test
    void deleteEmployee_existing_returns204() throws Exception {
        String id = createSampleEmployee();

        mockMvc.perform(delete("/api/employees/" + id))
                .andExpect(status().isNoContent());

        // Verify it's gone
        mockMvc.perform(get("/api/employees/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEmployee_nonExisting_returns404() throws Exception {
        mockMvc.perform(delete("/api/employees/999"))
                .andExpect(status().isNotFound());
    }

    // --- Search ---

    @Test
    void searchEmployees_byName_returnsMatchingResults() throws Exception {
        createSampleEmployee(); // Jane Smith
        createEmployeeWithEmail("john@example.com", "John Doe");

        mockMvc.perform(get("/api/employees/search")
                        .param("name", "Jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Jane Smith")));
    }

    @Test
    void searchEmployees_caseInsensitive_returnsMatchingResults() throws Exception {
        createSampleEmployee(); // Jane Smith

        mockMvc.perform(get("/api/employees/search")
                        .param("name", "jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Jane Smith")));
    }

    @Test
    void searchEmployees_noMatch_returnsEmptyPage() throws Exception {
        createSampleEmployee();

        mockMvc.perform(get("/api/employees/search")
                        .param("name", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void searchEmployees_blankName_returns400() throws Exception {
        mockMvc.perform(get("/api/employees/search")
                        .param("name", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Search name must not be blank"));
    }

    @Test
    void searchEmployees_whitespaceName_returns400() throws Exception {
        mockMvc.perform(get("/api/employees/search")
                        .param("name", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Search name must not be blank"));
    }

    // --- Pagination & Sorting Validation ---

    @Test
    void getAllEmployees_invalidPage_returns400() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page index must not be less than zero"));
    }

    @Test
    void getAllEmployees_invalidSizeZero_returns400() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page size must not be less than one"));
    }

    @Test
    void getAllEmployees_invalidSizeTooLarge_returns400() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page size must not exceed 100"));
    }

    @Test
    void getAllEmployees_invalidSortBy_returns400() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("sortBy", "invalidField"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid sort field: invalidField"));
    }

    @Test
    void getAllEmployees_invalidSortDir_returns400() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("sortDir", "invalidDir"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid sort direction: invalidDir"));
    }

    // --- Duplicate Email ---

    @Test
    void createEmployee_duplicateEmail_returns409() throws Exception {
        createSampleEmployee(); // creates jane@example.com

        EmployeeRequest request = new EmployeeRequest(
                "Jane Copy", "jane@example.com", "Marketing",
                new BigDecimal("80000.00"), EmployeeStatus.ACTIVE
        );

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Employee with email already exists: jane@example.com"));
    }

    @Test
    void updateEmployee_duplicateEmail_returns409() throws Exception {
        String id1 = createSampleEmployee(); // creates jane@example.com
        String id2 = createEmployeeWithEmail("john@example.com", "John Doe");

        EmployeeRequest updateRequest = new EmployeeRequest(
                "John Doe", "jane@example.com", "Engineering",
                new BigDecimal("75000.00"), EmployeeStatus.ACTIVE
        );

        mockMvc.perform(put("/api/employees/" + id2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Employee with email already exists: jane@example.com"));
    }

    @Test
    void updateEmployee_sameEmail_returns200() throws Exception {
        String id = createSampleEmployee(); // creates jane@example.com

        EmployeeRequest updateRequest = new EmployeeRequest(
                "Jane Updated", "jane@example.com", "Marketing",
                new BigDecimal("85000.00"), EmployeeStatus.ACTIVE
        );

        mockMvc.perform(put("/api/employees/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Updated")));
    }

    // --- Helper methods ---

    private String createSampleEmployee() throws Exception {
        return createEmployeeWithEmail("jane@example.com", "Jane Smith");
    }

    private String createEmployeeWithEmail(String email, String name) throws Exception {
        EmployeeRequest request = new EmployeeRequest(
                name, email, "Engineering",
                new BigDecimal("75000.00"), EmployeeStatus.ACTIVE
        );

        String responseBody = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseBody).get("id").asText();
    }
}
