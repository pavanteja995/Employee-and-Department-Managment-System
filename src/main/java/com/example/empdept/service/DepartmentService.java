package com.example.empdept.service;

import com.example.empdept.dto.DepartmentDetailResponse;
import com.example.empdept.dto.DepartmentRequest;
import com.example.empdept.dto.DepartmentResponse;
import com.example.empdept.dto.EmployeeResponse;
import com.example.empdept.entity.Department;
import com.example.empdept.entity.Employee;
import com.example.empdept.exception.DuplicateResourceException;
import com.example.empdept.exception.ResourceNotFoundException;
import com.example.empdept.repository.DepartmentRepository;
import com.example.empdept.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        String trimmedName = request.name() != null ? request.name().trim() : "";
        if (departmentRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Department '" + trimmedName + "' already exists");
        }
        String trimmedLocation = request.location() != null ? request.location().trim() : null;
        return toResponse(departmentRepository.save(new Department(trimmedName, trimmedLocation)));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream().map(DepartmentService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public DepartmentDetailResponse getDetails(Long id) {
        Department d = departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        List<EmployeeResponse> employees = d.getEmployees().stream().map(EmployeeService::toResponse).toList();
        return new DepartmentDetailResponse(d.getId(), d.getName(), d.getLocation(), employees);
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findOrThrow(id);
        String trimmedName = request.name() != null ? request.name().trim() : "";
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Department '" + trimmedName + "' already exists");
        }
        department.setName(trimmedName);
        department.setLocation(request.location() != null ? request.location().trim() : null);
        return toResponse(departmentRepository.save(department));
    }

    @Transactional
    public void delete(Long id) {
        Department department = findOrThrow(id);
        if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
            throw new IllegalStateException("Cannot delete department with id " + id + " because it still has assigned employees");
        }
        departmentRepository.delete(department);
    }

    @Transactional
    public EmployeeResponse assignEmployee(Long departmentId, Long employeeId) {
        Department department = findOrThrow(departmentId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        employee.setDepartment(department);
        return EmployeeService.toResponse(employeeRepository.save(employee));
    }

    private Department findOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    public static DepartmentResponse toResponse(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getLocation());
    }
}
