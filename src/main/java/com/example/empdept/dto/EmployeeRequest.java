package com.example.empdept.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record EmployeeRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotNull(message = "Salary is required")
        @Positive(message = "Salary must be greater than 0")
        BigDecimal salary,

        Long departmentId
) {}