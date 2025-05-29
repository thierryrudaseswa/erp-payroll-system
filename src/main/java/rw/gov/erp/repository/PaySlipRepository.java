package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rw.gov.erp.entity.PaySlip;

import java.util.List;
import java.util.Optional;

public interface PaySlipRepository extends JpaRepository<PaySlip, String> {
    @Query("SELECT p FROM PaySlip p WHERE p.employee.code = :employeeCode AND p.status = :status")
    List<PaySlip> findByEmployeeCodeAndStatus(String employeeCode, PaySlip.PaySlipStatus status);
    
    @Query("SELECT p FROM PaySlip p WHERE p.month = :month AND p.year = :year")
    List<PaySlip> findByMonthAndYear(Integer month, Integer year);
    
    @Query("SELECT COUNT(p) > 0 FROM PaySlip p WHERE p.employee.code = :employeeCode AND p.month = :month AND p.year = :year")
    boolean existsByEmployeeCodeAndMonthAndYear(String employeeCode, Integer month, Integer year);
} 