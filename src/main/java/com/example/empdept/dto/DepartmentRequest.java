package com.example.empdept.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @NotBlank(message = "Department name is required") String name,
        String location
) {}