package com.twopir.employeemanagement.dto;

import com.twopir.employeemanagement.entity.Employee;
import com.twopir.employeemanagement.entity.EmployeeStatus;
import java.math.BigDecimal;

public class EmployeeResponse {

    private Long id;
    private String name;
    private String email;
    private String department;
    private BigDecimal salary;
    private EmployeeStatus status;

    public EmployeeResponse() {
    }

    public EmployeeResponse(Long id, String name, String email, String department,
                            BigDecimal salary, EmployeeStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.salary = salary;
        this.status = status;
    }

    public static EmployeeResponse fromEntity(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getStatus()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
