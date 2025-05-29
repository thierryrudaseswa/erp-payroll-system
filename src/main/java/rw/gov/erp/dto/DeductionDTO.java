package rw.gov.erp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeductionDTO {
    private String code;
    
    @NotBlank(message = "Deduction name is required")
    private String deductionName;
    
    @NotNull(message = "Percentage is required")
    @Min(value = 0, message = "Percentage must be between 0 and 100")
    @Max(value = 100, message = "Percentage must be between 0 and 100")
    private Double percentage;
} 