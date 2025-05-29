package rw.gov.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pay_slips", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_code", "month", "year"})
})
public class PaySlip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_code", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private BigDecimal houseAmount;

    @Column(nullable = false)
    private BigDecimal transportAmount;

    @Column(nullable = false)
    private BigDecimal employeeTaxedAmount;

    @Column(nullable = false)
    private BigDecimal pensionAmount;

    @Column(nullable = false)
    private BigDecimal medicalInsuranceAmount;

    @Column(nullable = false)
    private BigDecimal otherTaxedAmount;

    @Column(nullable = false)
    private BigDecimal grossSalary;

    @Column(nullable = false)
    private BigDecimal netSalary;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaySlipStatus status = PaySlipStatus.PENDING;

    public enum PaySlipStatus {
        PENDING, PAID
    }
} 