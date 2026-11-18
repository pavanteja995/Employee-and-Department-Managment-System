package com.example.empdept.repository;

import com.example.empdept.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findBySalaryGreaterThan(BigDecimal salary);

    List<Employee> findByNameContainingIgnoreCase(String keyword);

    List<Employee> findBySalaryBetween(BigDecimal min, BigDecimal max);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    // custom JPQL
    @Query("SELECT e FROM Employee e WHERE LOWER(e.department.name) = LOWER(:departmentName)")
    List<Employee> findByDepartmentName(@Param("departmentName") String departmentName);

    // native SQL
    @Query(value = "SELECT * FROM employees ORDER BY salary DESC LIMIT :limit", nativeQuery = true)
    List<Employee> findTopPaidEmployees(@Param("limit") int limit);
}