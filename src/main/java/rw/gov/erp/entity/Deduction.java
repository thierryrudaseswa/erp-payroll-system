package rw.gov.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deductions")
public class Deduction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String code;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String deductionName;

    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private Double percentage;
} 