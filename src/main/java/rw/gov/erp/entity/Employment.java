package rw.gov.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "employments")
public class Employment {

    @Id
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_code", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private BigDecimal baseSalary;

    @Column(nullable = false)
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus status;

    public enum EmploymentStatus {
        ACTIVE, INACTIVE
    }

    @PrePersist
    public void prePersist() {
        if (code == null) {
            code = "EMPL" + System.currentTimeMillis();
        }
    }
} 