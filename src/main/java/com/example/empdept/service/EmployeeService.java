package com.example.empdept.service;

import com.example.empdept.dto.EmployeeRequest;
import com.example.empdept.dto.EmployeeResponse;
import com.example.empdept.entity.Department;
import com.example.empdept.entity.Employee;
import com.example.empdept.exception.DuplicateResourceException;
import com.example.empdept.exception.ResourceNotFoundException;
import com.example.empdept.repository.DepartmentRepository;
import com.example.empdept.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeService {

    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "name", "email", "salary");

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Employee with email " + request.email() + " already exists");
        }
        Employee employee = new Employee();
        apply(employee, request);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAll(int page, int size, String sortBy, String direction) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 100");
        }
        if (!SORTABLE_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("sortBy must be one of " + SORTABLE_FIELDS);
        }
        Sort sort = "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return employeeRepository.findAll(PageRequest.of(page, size, sort)).map(EmployeeService::toResponse);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findOrThrow(id);
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Employee with email " + request.email() + " already exists");
        }
        apply(employee, request);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void delete(Long id) {
        employeeRepository.delete(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getByDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found with id: " + departmentId);
        }
        return employeeRepository.findByDepartmentId(departmentId).stream()
                .map(EmployeeService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getBySalaryGreaterThan(BigDecimal amount) {
        return employeeRepository.findBySalaryGreaterThan(amount).stream()
                .map(EmployeeService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchByName(String keyword) {
        return employeeRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(EmployeeService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getBySalaryBetween(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min must not be greater than max");
        }
        return employeeRepository.findBySalaryBetween(min, max).stream()
                .map(EmployeeService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getByDepartmentName(String departmentName) {
        return employeeRepository.findByDepartmentName(departmentName).stream()
                .map(EmployeeService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getTopPaid(int limit) {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("limit must be between 1 and 100");
        }
        return employeeRepository.findTopPaidEmployees(limit).stream()
                .map(EmployeeService::toResponse).toList();
    }

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private void apply(Employee employee, EmployeeRequest request) {
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setSalary(request.salary());
        if (request.departmentId() != null) {
            Department department = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department not found with id: " + request.departmentId()));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }
    }

    public static EmployeeResponse toResponse(Employee e) {
        Department d = e.getDepartment();
        return new EmployeeResponse(e.getId(), e.getName(), e.getEmail(), e.getSalary(),
                d != null ? d.getId() : null,
                d != null ? d.getName() : null);
    }
}