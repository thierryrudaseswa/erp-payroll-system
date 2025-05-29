package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.erp.entity.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);
} 