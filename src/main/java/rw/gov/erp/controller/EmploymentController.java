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
import rw.gov.erp.dto.EmploymentDTO;
import rw.gov.erp.service.EmploymentService;

import java.util.List;

@RestController
@RequestMapping("/api/employments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employment", description = "Employment and job details management APIs")
public class EmploymentController {

    private final EmploymentService employmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(
        summary = "Create employment record",
        description = "Creates a new employment record for an employee. Requires MANAGER or ADMIN role.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Employment record created successfully",
                content = @Content(schema = @Schema(implementation = EmploymentDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Not authorized to create employment records"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
        }
    )
    public ResponseEntity<EmploymentDTO> createEmployment(@Valid @RequestBody EmploymentDTO employmentDTO) {
        return ResponseEntity.ok(employmentService.createEmployment(employmentDTO));
    }

    @GetMapping("/employee/{employeeCode}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @securityService.isCurrentUser(#employeeCode)")
    @Operation(
        summary = "Get employment by employee code",
        description = "Retrieves employment details for a specific employee. Requires MANAGER/ADMIN role or to be the employee.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved employment details",
                content = @Content(schema = @Schema(implementation = EmploymentDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "Not authorized to view this employment record"),
            @ApiResponse(responseCode = "404", description = "Employment record not found")
        }
    )
    public ResponseEntity<EmploymentDTO> getEmploymentByEmployeeCode(@PathVariable String employeeCode) {
        return ResponseEntity.ok(employmentService.getEmploymentByEmployeeCode(employeeCode));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(
        summary = "Get all employment records",
        description = "Retrieves all employment records. Requires MANAGER or ADMIN role.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved employment records",
                content = @Content(schema = @Schema(implementation = EmploymentDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "Not authorized to view employment records")
        }
    )
    public ResponseEntity<List<EmploymentDTO>> getAllEmployments() {
        return ResponseEntity.ok(employmentService.getAllEmployments());
    }

    @PutMapping("/{code}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(
        summary = "Update employment record",
        description = "Updates an existing employment record. Requires MANAGER or ADMIN role.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Employment record updated successfully",
                content = @Content(schema = @Schema(implementation = EmploymentDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update employment records"),
            @ApiResponse(responseCode = "404", description = "Employment record not found")
        }
    )
    public ResponseEntity<EmploymentDTO> updateEmployment(
            @PathVariable String code,
            @Valid @RequestBody EmploymentDTO employmentDTO) {
        return ResponseEntity.ok(employmentService.updateEmployment(code, employmentDTO));
    }
} 