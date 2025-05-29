package rw.gov.erp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.dto.EmployeeDTO;
import rw.gov.erp.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employee", description = "Employee management APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(
        summary = "Create a new employee",
        description = "Creates a new employee in the system. Requires MANAGER or ADMIN role.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Employee created successfully",
                content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Not authorized to create employees")
        }
    )
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.createEmployee(employeeDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(
        summary = "Get all employees",
        description = "Retrieves a list of all employees. Requires MANAGER or ADMIN role.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved employee list",
                content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "Not authorized to view employees")
        }
    )
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @securityService.isCurrentUser(#code)")
    @Operation(
        summary = "Get employee by code",
        description = "Retrieves an employee by their unique code. Requires MANAGER/ADMIN role or to be the employee.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved employee",
                content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "Not authorized to view this employee"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
        }
    )
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable String code) {
        return ResponseEntity.ok(employeeService.getEmployee(code));
    }

    @PutMapping("/{code}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @securityService.isCurrentUser(#code)")
    @Operation(
        summary = "Update employee",
        description = "Updates an existing employee's information. Requires MANAGER/ADMIN role or to be the employee.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Employee updated successfully",
                content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update this employee"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
        }
    )
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable String code,
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.updateEmployee(code, employeeDTO));
    }
} 