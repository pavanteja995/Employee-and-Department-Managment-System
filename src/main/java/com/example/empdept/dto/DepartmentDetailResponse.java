package com.example.empdept.dto;

import java.util.List;

public record DepartmentDetailResponse(
        Long id, String name, String location, List<EmployeeResponse> employees
) {}