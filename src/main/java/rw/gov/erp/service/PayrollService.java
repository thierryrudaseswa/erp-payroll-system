package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.PaySlipDTO;
import rw.gov.erp.entity.Deduction;
import rw.gov.erp.entity.Employment;
import rw.gov.erp.entity.PaySlip;
import rw.gov.erp.exception.ResourceNotFoundException;
import rw.gov.erp.repository.DeductionRepository;
import rw.gov.erp.repository.EmploymentRepository;
import rw.gov.erp.repository.PaySlipRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PaySlipRepository paySlipRepository;
    private final EmploymentRepository employmentRepository;
    private final DeductionRepository deductionRepository;
    private final MessageService messageService;

    @Transactional
    public List<PaySlipDTO> processPayroll(Integer month, Integer year) {
        // Get all active employments
        List<Employment> activeEmployments = employmentRepository.findAll().stream()
                .filter(e -> e.getStatus() == Employment.EmploymentStatus.ACTIVE)
                .toList();

        if (activeEmployments.isEmpty()) {
            throw new IllegalStateException("No active employments found");
        }

        // Get all required deductions
        Map<String, Deduction> deductions = getRequiredDeductions();

        return activeEmployments.stream()
                .map(employment -> createPaySlip(employment, month, year, deductions))
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public void approvePayroll(Integer month, Integer year) {
        List<PaySlip> paySlips = paySlipRepository.findByMonthAndYear(month, year);
        
        if (paySlips.isEmpty()) {
            throw new ResourceNotFoundException(
                String.format("No payslips found for month %d and year %d", month, year));
        }

        paySlips.forEach(paySlip -> {
            paySlip.setStatus(PaySlip.PaySlipStatus.PAID);
            messageService.createPaymentMessage(paySlip);
        });
        paySlipRepository.saveAll(paySlips);
    }

    public List<PaySlipDTO> getEmployeePaySlips(String employeeCode) {
        return paySlipRepository.findByEmployeeCodeAndStatus(employeeCode, PaySlip.PaySlipStatus.PAID)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<PaySlipDTO> getEmployeePendingPaySlips(String employeeCode) {
        return paySlipRepository.findByEmployeeCodeAndStatus(employeeCode, PaySlip.PaySlipStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<PaySlipDTO> getMonthlyPaySlips(Integer month, Integer year) {
        List<PaySlip> paySlips = paySlipRepository.findByMonthAndYear(month, year);
        
        if (paySlips.isEmpty()) {
            throw new ResourceNotFoundException(
                String.format("No payslips found for month %d and year %d", month, year));
        }

        return paySlips.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private PaySlip createPaySlip(Employment employment, Integer month, Integer year, Map<String, Deduction> deductions) {
        // Check if payslip already exists
        if (paySlipRepository.existsByEmployeeCodeAndMonthAndYear(
                employment.getEmployee().getCode(), month, year)) {
            throw new IllegalStateException(
                String.format("Payslip already exists for employee %s for month %d and year %d",
                    employment.getEmployee().getCode(), month, year));
        }

        BigDecimal baseSalary = employment.getBaseSalary();
        
        // Calculate housing and transport amounts (14% each of base salary)
        BigDecimal housingAmount = calculatePercentage(baseSalary, 14.0);
        BigDecimal transportAmount = calculatePercentage(baseSalary, 14.0);
        
        // Calculate gross salary
        BigDecimal grossSalary = baseSalary.add(housingAmount).add(transportAmount);

        // Calculate deductions
        BigDecimal employeeTax = calculateDeduction(baseSalary, deductions, "EmployeeTax");
        BigDecimal pension = calculateDeduction(baseSalary, deductions, "Pension");
        BigDecimal medicalInsurance = calculateDeduction(baseSalary, deductions, "MedicalInsurance");
        BigDecimal others = calculateDeduction(baseSalary, deductions, "Others");

        // Calculate net salary
        BigDecimal totalDeductions = employeeTax.add(pension).add(medicalInsurance).add(others);
        BigDecimal netSalary = grossSalary.subtract(totalDeductions);

        PaySlip paySlip = new PaySlip();
        paySlip.setEmployee(employment.getEmployee());
        paySlip.setHouseAmount(housingAmount);
        paySlip.setTransportAmount(transportAmount);
        paySlip.setEmployeeTaxedAmount(employeeTax);
        paySlip.setPensionAmount(pension);
        paySlip.setMedicalInsuranceAmount(medicalInsurance);
        paySlip.setOtherTaxedAmount(others);
        paySlip.setGrossSalary(grossSalary);
        paySlip.setNetSalary(netSalary);
        paySlip.setMonth(month);
        paySlip.setYear(year);
        paySlip.setStatus(PaySlip.PaySlipStatus.PENDING);

        return paySlipRepository.save(paySlip);
    }

    private Map<String, Deduction> getRequiredDeductions() {
        Map<String, Deduction> deductions = deductionRepository.findAll().stream()
                .collect(Collectors.toMap(Deduction::getDeductionName, Function.identity()));

        // Verify all required deductions exist
        List<String> requiredDeductions = List.of("EmployeeTax", "Pension", "MedicalInsurance", "Others", "Housing", "Transport");
        List<String> missingDeductions = requiredDeductions.stream()
                .filter(name -> !deductions.containsKey(name))
                .toList();

        if (!missingDeductions.isEmpty()) {
            throw new IllegalStateException("Missing required deductions: " + missingDeductions);
        }

        return deductions;
    }

    private BigDecimal calculateDeduction(BigDecimal amount, Map<String, Deduction> deductions, String deductionName) {
        return Optional.ofNullable(deductions.get(deductionName))
                .map(Deduction::getPercentage)
                .map(percentage -> calculatePercentage(amount, percentage))
                .orElseThrow(() -> new IllegalStateException("Deduction not found: " + deductionName));
    }

    private BigDecimal calculatePercentage(BigDecimal amount, Double percentage) {
        return amount.multiply(BigDecimal.valueOf(percentage / 100.0))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private PaySlipDTO convertToDTO(PaySlip paySlip) {
        PaySlipDTO dto = new PaySlipDTO();
        dto.setId(paySlip.getId());
        dto.setEmployeeCode(paySlip.getEmployee().getCode());
        dto.setHouseAmount(paySlip.getHouseAmount());
        dto.setTransportAmount(paySlip.getTransportAmount());
        dto.setEmployeeTaxedAmount(paySlip.getEmployeeTaxedAmount());
        dto.setPensionAmount(paySlip.getPensionAmount());
        dto.setMedicalInsuranceAmount(paySlip.getMedicalInsuranceAmount());
        dto.setOtherTaxedAmount(paySlip.getOtherTaxedAmount());
        dto.setGrossSalary(paySlip.getGrossSalary());
        dto.setNetSalary(paySlip.getNetSalary());
        dto.setMonth(paySlip.getMonth());
        dto.setYear(paySlip.getYear());
        dto.setStatus(paySlip.getStatus());
        return dto;
    }
} 