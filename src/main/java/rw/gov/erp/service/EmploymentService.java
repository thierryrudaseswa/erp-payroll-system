package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.EmploymentDTO;
import rw.gov.erp.entity.Employee;
import rw.gov.erp.entity.Employment;
import rw.gov.erp.exception.ResourceNotFoundException;
import rw.gov.erp.repository.EmployeeRepository;
import rw.gov.erp.repository.EmploymentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmploymentDTO createEmployment(EmploymentDTO employmentDTO) {
        Employee employee = employeeRepository.findById(employmentDTO.getEmployeeCode())
                .orElseThrow(() -> ResourceNotFoundException.employeeNotFound(employmentDTO.getEmployeeCode()));

        Employment employment = new Employment();
        updateEmploymentFromDTO(employment, employmentDTO);
        employment.setEmployee(employee);
        employment.setStatus(Employment.EmploymentStatus.ACTIVE);

        Employment savedEmployment = employmentRepository.save(employment);
        return convertToDTO(savedEmployment);
    }

    public EmploymentDTO getEmploymentByEmployeeCode(String employeeCode) {
        return employmentRepository.findByEmployeeCode(employeeCode)
                .map(this::convertToDTO)
                .orElseThrow(() -> ResourceNotFoundException.employmentNotFound(employeeCode));
    }

    public List<EmploymentDTO> getAllEmployments() {
        return employmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public EmploymentDTO updateEmployment(String code, EmploymentDTO employmentDTO) {
        Employment employment = employmentRepository.findById(code)
                .orElseThrow(() -> ResourceNotFoundException.employmentNotFound(code));

        updateEmploymentFromDTO(employment, employmentDTO);
        Employment updatedEmployment = employmentRepository.save(employment);
        return convertToDTO(updatedEmployment);
    }

    private void updateEmploymentFromDTO(Employment employment, EmploymentDTO dto) {
        employment.setPosition(dto.getPosition());
        employment.setDepartment(dto.getDepartment());
        employment.setBaseSalary(dto.getBaseSalary());
        employment.setJoiningDate(dto.getJoiningDate());
        if (dto.getStatus() != null) {
            employment.setStatus(dto.getStatus());
        }
    }

    private EmploymentDTO convertToDTO(Employment employment) {
        EmploymentDTO dto = new EmploymentDTO();
        dto.setCode(employment.getCode());
        dto.setEmployeeCode(employment.getEmployee().getCode());
        dto.setPosition(employment.getPosition());
        dto.setDepartment(employment.getDepartment());
        dto.setBaseSalary(employment.getBaseSalary());
        dto.setJoiningDate(employment.getJoiningDate());
        dto.setStatus(employment.getStatus());
        return dto;
    }
} 