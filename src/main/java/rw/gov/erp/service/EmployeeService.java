package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.EmployeeDTO;
import rw.gov.erp.entity.Employee;
import rw.gov.erp.exception.ResourceNotFoundException;
import rw.gov.erp.repository.EmployeeRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + employeeDTO.getEmail());
        }

        Employee employee = new Employee();
        updateEmployeeFromDTO(employee, employeeDTO);
        employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        employee.setStatus(Employee.EmployeeStatus.ACTIVE);

        Set<Employee.Role> roles = employeeDTO.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Employee.Role.ROLE_EMPLOYEE);
        }
        employee.setRoles(roles);

        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    public EmployeeDTO getEmployee(String code) {
        return employeeRepository.findById(code)
                .map(this::convertToDTO)
                .orElseThrow(() -> ResourceNotFoundException.employeeNotFound(code));
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public EmployeeDTO updateEmployee(String code, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(code)
                .orElseThrow(() -> ResourceNotFoundException.employeeNotFound(code));

        updateEmployeeFromDTO(employee, employeeDTO);
        
        if (employeeDTO.getStatus() != null) {
            employee.setStatus(employeeDTO.getStatus());
        }
        
        if (employeeDTO.getRoles() != null && !employeeDTO.getRoles().isEmpty()) {
            employee.setRoles(employeeDTO.getRoles());
        }

        if (employeeDTO.getPassword() != null && !employeeDTO.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDTO(updatedEmployee);
    }

    private void updateEmployeeFromDTO(Employee employee, EmployeeDTO dto) {
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setMobile(dto.getMobile());
        employee.setDateOfBirth(dto.getDateOfBirth());
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setCode(employee.getCode());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setMobile(employee.getMobile());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setStatus(employee.getStatus());
        dto.setRoles(employee.getRoles());
        return dto;
    }
} 