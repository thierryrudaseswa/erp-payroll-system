package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.erp.entity.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findBySentFalse();
    List<Message> findByEmployeeCodeAndMonthAndYear(String employeeCode, Integer month, Integer year);
} 