package com.example.empdept.controller;

import com.example.empdept.dto.EmployeeRequest;
import com.example.empdept.dto.EmployeeResponse;
import com.example.empdept.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @GetMapping
    public ResponseEntity<PagedModel<EmployeeResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(new PagedModel<>(employeeService.getAll(page, size, sortBy, direction)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeResponse>> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(employeeService.getByDepartment(departmentId));
    }

    @GetMapping("/salary/greater-than")
    public ResponseEntity<List<EmployeeResponse>> salaryGreaterThan(@RequestParam BigDecimal amount) {
        return ResponseEntity.ok(employeeService.getBySalaryGreaterThan(amount));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(employeeService.searchByName(keyword));
    }

    @GetMapping("/salary/between")
    public ResponseEntity<List<EmployeeResponse>> salaryBetween(@RequestParam BigDecimal min,
                                                                @RequestParam BigDecimal max) {
        return ResponseEntity.ok(employeeService.getBySalaryBetween(min, max));
    }

    @GetMapping("/by-department-name")
    public ResponseEntity<List<EmployeeResponse>> byDepartmentName(@RequestParam String name) {
        return ResponseEntity.ok(employeeService.getByDepartmentName(name));
    }

    @GetMapping("/top-paid")
    public ResponseEntity<List<EmployeeResponse>> topPaid(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(employeeService.getTopPaid(limit));
    }
}