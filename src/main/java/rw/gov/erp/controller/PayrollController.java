package rw.gov.erp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.dto.PaySlipDTO;
import rw.gov.erp.service.PayrollService;
import rw.gov.erp.service.PdfService;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payroll", description = "Payroll management APIs")
public class PayrollController {

    private final PayrollService payrollService;
    private final PdfService pdfService;

    @PostMapping("/process")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
        summary = "Process payroll for a given month and year",
        description = "Processes payroll for all active employees for the specified month and year. Requires MANAGER role.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Payroll processed successfully",
                content = @Content(schema = @Schema(implementation = PaySlipDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "Not authorized to process payroll"),
            @ApiResponse(responseCode = "400", description = "Invalid month or year")
        }
    )
    public ResponseEntity<List<PaySlipDTO>> processPayroll(@Valid @RequestBody PayrollRequest request) {
        return ResponseEntity.ok(payrollService.processPayroll(request.getMonth(), request.getYear()));
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Approve payroll for a given month and year",
        description = "Approves all pending payslips for the specified month and year. Requires ADMIN role.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Payroll approved successfully"),
            @ApiResponse(responseCode = "403", description = "Not authorized to approve payroll"),
            @ApiResponse(responseCode = "404", description = "No payslips found for the specified month and year")
        }
    )
    public ResponseEntity<Void> approvePayroll(@Valid @RequestBody PayrollRequest request) {
        payrollService.approvePayroll(request.getMonth(), request.getYear());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/employee/{employeeCode}")
    @PreAuthorize("hasAnyRole('MANAGER') or @securityService.isCurrentUser(#employeeCode)")
    @Operation(
        summary = "Get employee payslips",
        description = "Retrieves all approved payslips for the specified employee. Requires MANAGER role or to be the employee.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved payslips",
                content = @Content(schema = @Schema(implementation = PaySlipDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "Not authorized to view these payslips"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
        }
    )
    public ResponseEntity<List<PaySlipDTO>> getEmployeePaySlips(
            @Parameter(description = "Employee's unique code", required = true)
            @PathVariable String employeeCode) {
        return ResponseEntity.ok(payrollService.getEmployeePaySlips(employeeCode));
    }

    @GetMapping("/employee/{employeeCode}/pending")
    @PreAuthorize("hasAnyRole('MANAGER') or @securityService.isCurrentUser(#employeeCode)")
    @Operation(
        summary = "Get employee pending payslips",
        description = "Retrieves all pending payslips for the specified employee. Requires MANAGER role or to be the employee.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved pending payslips",
                content = @Content(schema = @Schema(implementation = PaySlipDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "Not authorized to view these payslips"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
        }
    )
    public ResponseEntity<List<PaySlipDTO>> getEmployeePendingPaySlips(
            @Parameter(description = "Employee's unique code", required = true)
            @PathVariable String employeeCode) {
        return ResponseEntity.ok(payrollService.getEmployeePendingPaySlips(employeeCode));
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
        summary = "Get all payslips for a specific month and year",
        description = "Retrieves all payslips for the specified month and year. Requires MANAGER role.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved payslips",
                content = @Content(schema = @Schema(implementation = PaySlipDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "Not authorized to view payslips"),
            @ApiResponse(responseCode = "400", description = "Invalid month or year")
        }
    )
    public ResponseEntity<List<PaySlipDTO>> getMonthlyPaySlips(
            @Parameter(description = "Month (1-12)", required = true)
            @RequestParam @Min(1) @Max(12) Integer month,
            @Parameter(description = "Year (2024 or later)", required = true)
            @RequestParam @Min(2024) Integer year) {
        return ResponseEntity.ok(payrollService.getMonthlyPaySlips(month, year));
    }

    @GetMapping("/download/{paySlipId}")
    @PreAuthorize("hasRole('MANAGER') or @securityService.isCurrentUserPaySlip(#paySlipId)")
    @Operation(
        summary = "Download payslip as PDF",
        description = "Downloads the specified payslip as a PDF file. Requires MANAGER role or to be the employee.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "PDF file downloaded successfully",
                content = @Content(mediaType = "application/pdf")
            ),
            @ApiResponse(responseCode = "403", description = "Not authorized to download this payslip"),
            @ApiResponse(responseCode = "404", description = "Payslip not found")
        }
    )
    public ResponseEntity<byte[]> downloadPaySlip(
            @Parameter(description = "PaySlip ID", required = true)
            @PathVariable String paySlipId) {
        byte[] pdfContent = pdfService.generatePaySlipPdf(paySlipId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"payslip.pdf\"")
                .body(pdfContent);
    }

    @Schema(description = "Request object for payroll processing and approval")
    @Data
    public static class PayrollRequest {
        @NotNull(message = "Month is required")
        @Min(value = 1, message = "Month must be between 1 and 12")
        @Max(value = 12, message = "Month must be between 1 and 12")
        @Schema(description = "Month (1-12)", example = "3")
        private Integer month;

        @NotNull(message = "Year is required")
        @Min(value = 2024, message = "Year must be 2024 or later")
        @Schema(description = "Year (2024 or later)", example = "2024")
        private Integer year;
    }
} 