package com.twopir.employeemanagement.controller;

import com.twopir.employeemanagement.dto.EmployeeRequest;
import com.twopir.employeemanagement.dto.EmployeeResponse;
import com.twopir.employeemanagement.exception.InvalidRequestException;
import com.twopir.employeemanagement.service.EmployeeService;
import java.util.Set;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "email", "department", "salary", "status");

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        if (page < 0) {
            throw new InvalidRequestException("Page index must not be less than zero");
        }
        if (size < 1) {
            throw new InvalidRequestException("Page size must not be less than one");
        }
        if (size > 100) {
            throw new InvalidRequestException("Page size must not exceed 100");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new InvalidRequestException("Invalid sort field: " + sortBy);
        }
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new InvalidRequestException("Invalid sort direction: " + sortDir);
        }

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmployeeResponse> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<EmployeeResponse>> searchEmployees(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRequestException("Search name must not be blank");
        }
        if (page < 0) {
            throw new InvalidRequestException("Page index must not be less than zero");
        }
        if (size < 1) {
            throw new InvalidRequestException("Page size must not be less than one");
        }
        if (size > 100) {
            throw new InvalidRequestException("Page size must not exceed 100");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeResponse> employees = employeeService.searchEmployeesByName(name.trim(), pageable);
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
