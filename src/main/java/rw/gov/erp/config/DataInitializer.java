package rw.gov.erp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rw.gov.erp.entity.Deduction;
import rw.gov.erp.entity.Employee;
import rw.gov.erp.repository.DeductionRepository;
import rw.gov.erp.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DeductionRepository deductionRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default deductions if they don't exist
        if (deductionRepository.count() == 0) {
            createDefaultDeductions();
        }

        // Create admin user if no users exist
        if (employeeRepository.count() == 0) {
            createAdminUser();
        }
    }

    private void createDefaultDeductions() {
        // Employee Tax (30%)
        Deduction employeeTax = new Deduction();
        employeeTax.setCode("TAX001");
        employeeTax.setDeductionName("EmployeeTax");
        employeeTax.setPercentage(30.0);
        deductionRepository.save(employeeTax);

        // Pension (6%)
        Deduction pension = new Deduction();
        pension.setCode("PEN001");
        pension.setDeductionName("Pension");
        pension.setPercentage(6.0);
        deductionRepository.save(pension);

        // Medical Insurance (5%)
        Deduction medical = new Deduction();
        medical.setCode("MED001");
        medical.setDeductionName("MedicalInsurance");
        medical.setPercentage(5.0);
        deductionRepository.save(medical);

        // Other Deductions (5%)
        Deduction others = new Deduction();
        others.setCode("OTH001");
        others.setDeductionName("Others");
        others.setPercentage(5.0);
        deductionRepository.save(others);

        // Housing Allowance (14%)
        Deduction housing = new Deduction();
        housing.setCode("HSG001");
        housing.setDeductionName("Housing");
        housing.setPercentage(14.0);
        deductionRepository.save(housing);

        // Transport Allowance (14%)
        Deduction transport = new Deduction();
        transport.setCode("TRN001");
        transport.setDeductionName("Transport");
        transport.setPercentage(14.0);
        deductionRepository.save(transport);
    }

    private void createAdminUser() {
        Employee admin = new Employee();
        admin.setCode("ADM001");
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setEmail("admin@erp.gov.rw");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setMobile("+250780000000");
        admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
        admin.setStatus(Employee.EmployeeStatus.ACTIVE);
        admin.setRoles(Set.of(Employee.Role.ROLE_ADMIN, Employee.Role.ROLE_MANAGER));
        
        employeeRepository.save(admin);
    }
} 