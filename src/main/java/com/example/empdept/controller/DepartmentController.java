package com.example.empdept.controller;

import com.example.empdept.dto.DepartmentDetailResponse;
import com.example.empdept.dto.DepartmentRequest;
import com.example.empdept.dto.DepartmentResponse;
import com.example.empdept.dto.EmployeeResponse;
import com.example.empdept.service.DepartmentService;
import com.example.empdept.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    public DepartmentController(DepartmentService departmentService, EmployeeService employeeService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAll() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<DepartmentDetailResponse> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDetails(id));
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getByDepartment(id));
    }

    @PutMapping("/{departmentId}/employees/{employeeId}")
    public ResponseEntity<EmployeeResponse> assignEmployee(@PathVariable Long departmentId,
                                                           @PathVariable Long employeeId) {
        return ResponseEntity.ok(departmentService.assignEmployee(departmentId, employeeId));
    }
}