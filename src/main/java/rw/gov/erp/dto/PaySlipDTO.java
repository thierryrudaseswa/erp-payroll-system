package rw.gov.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import rw.gov.erp.entity.PaySlip;

import java.math.BigDecimal;

@Data
public class PaySlipDTO {
    private String id;
    
    @NotBlank(message = "Employee code is required")
    private String employeeCode;
    
    private BigDecimal houseAmount;
    private BigDecimal transportAmount;
    private BigDecimal employeeTaxedAmount;
    private BigDecimal pensionAmount;
    private BigDecimal medicalInsuranceAmount;
    private BigDecimal otherTaxedAmount;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    
    @NotNull(message = "Month is required")
    private Integer month;
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    private PaySlip.PaySlipStatus status;
} 