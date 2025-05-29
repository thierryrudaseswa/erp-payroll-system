package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.erp.entity.Deduction;

import java.util.Optional;

public interface DeductionRepository extends JpaRepository<Deduction, String> {
    Optional<Deduction> findByDeductionName(String deductionName);
    boolean existsByDeductionName(String deductionName);
} 