package com.twopir.employeemanagement.repository;

import com.twopir.employeemanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
