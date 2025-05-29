package rw.gov.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import rw.gov.erp.entity.Employment;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Employment record details")
public class EmploymentDTO {
    
    @Schema(description = "Unique employment record code", example = "EMP001")
    private String code;

    @NotBlank(message = "Employee code is required")
    @Schema(description = "Employee code", example = "EMP001")
    private String employeeCode;

    @NotBlank(message = "Position is required")
    @Schema(description = "Job position", example = "Software Engineer")
    private String position;

    @NotBlank(message = "Department is required")
    @Schema(description = "Department name", example = "IT")
    private String department;

    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be positive")
    @Schema(description = "Monthly base salary", example = "1000000.00")
    private BigDecimal baseSalary;

    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date cannot be in the future")
    @Schema(description = "Employment start date", example = "2024-03-01")
    private LocalDate joiningDate;

    @Schema(description = "Employment status", example = "ACTIVE")
    private Employment.EmploymentStatus status = Employment.EmploymentStatus.ACTIVE;
} 