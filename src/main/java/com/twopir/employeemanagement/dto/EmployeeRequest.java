package com.twopir.employeemanagement.dto;

import com.twopir.employeemanagement.entity.EmployeeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class EmployeeRequest {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Department must not be blank")
    private String department;

    @NotNull(message = "Salary must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Salary must be zero or positive")
    private BigDecimal salary;

    @NotNull(message = "Status must not be null (accepted values: ACTIVE, INACTIVE)")
    private EmployeeStatus status;

    public EmployeeRequest() {
    }

    public EmployeeRequest(String name, String email, String department, BigDecimal salary, EmployeeStatus status) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.salary = salary;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }
}
