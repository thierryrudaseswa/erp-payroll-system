package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rw.gov.erp.entity.Employment;

import java.util.List;
import java.util.Optional;

public interface EmploymentRepository extends JpaRepository<Employment, String> {
    List<Employment> findByEmployeeCodeAndStatus(String employeeCode, Employment.EmploymentStatus status);

    @Query("SELECT e FROM Employment e WHERE e.employee.code = :employeeCode")
    Optional<Employment> findByEmployeeCode(String employeeCode);
} 