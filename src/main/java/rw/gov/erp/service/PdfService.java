package rw.gov.erp.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.gov.erp.entity.Employee;
import rw.gov.erp.entity.PaySlip;
import rw.gov.erp.exception.ResourceNotFoundException;
import rw.gov.erp.repository.PaySlipRepository;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final PaySlipRepository paySlipRepository;
    private static final String[] MONTHS = new String[] {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public byte[] generatePaySlipPdf(String paySlipId) {
        PaySlip paySlip = paySlipRepository.findById(paySlipId)
                .orElseThrow(() -> ResourceNotFoundException.paySlipNotFound(paySlipId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Header
            addHeader(document, paySlip);
            
            // Employee Details
            addEmployeeDetails(document, paySlip.getEmployee());
            
            // Salary Details
            addSalaryDetails(document, paySlip);
            
            // Footer
            addFooter(document, paySlip);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private void addHeader(Document document, PaySlip paySlip) {
        Paragraph header = new Paragraph("Government of Rwanda")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16)
                .setBold();
        document.add(header);

        Paragraph subHeader = new Paragraph("Payslip for " + MONTHS[paySlip.getMonth() - 1] + " " + paySlip.getYear())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14);
        document.add(subHeader);
        
        document.add(new Paragraph("\n"));
    }

    private void addEmployeeDetails(Document document, Employee employee) {
        float[] columnWidths = {30f, 70f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createCell("Employee Code:", true));
        table.addCell(createCell(employee.getCode(), false));

        table.addCell(createCell("Name:", true));
        table.addCell(createCell(employee.getFirstName() + " " + employee.getLastName(), false));

        table.addCell(createCell("Email:", true));
        table.addCell(createCell(employee.getEmail(), false));

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addSalaryDetails(Document document, PaySlip paySlip) {
        float[] columnWidths = {70f, 30f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Earnings
        Cell earningsHeader = new Cell(1, 2).add(new Paragraph("Earnings")).setBold();
        table.addCell(earningsHeader);
        
        table.addCell(createCell("Basic Salary", false));
        table.addCell(createCell(formatAmount(paySlip.getGrossSalary()
                .subtract(paySlip.getHouseAmount())
                .subtract(paySlip.getTransportAmount())), false));

        table.addCell(createCell("Housing Allowance", false));
        table.addCell(createCell(formatAmount(paySlip.getHouseAmount()), false));

        table.addCell(createCell("Transport Allowance", false));
        table.addCell(createCell(formatAmount(paySlip.getTransportAmount()), false));

        table.addCell(createCell("Gross Salary", true));
        table.addCell(createCell(formatAmount(paySlip.getGrossSalary()), true));

        // Deductions
        Cell deductionsHeader = new Cell(1, 2).add(new Paragraph("Deductions")).setBold();
        table.addCell(deductionsHeader);

        table.addCell(createCell("Tax", false));
        table.addCell(createCell(formatAmount(paySlip.getEmployeeTaxedAmount()), false));

        table.addCell(createCell("Pension", false));
        table.addCell(createCell(formatAmount(paySlip.getPensionAmount()), false));

        table.addCell(createCell("Medical Insurance", false));
        table.addCell(createCell(formatAmount(paySlip.getMedicalInsuranceAmount()), false));

        table.addCell(createCell("Other Deductions", false));
        table.addCell(createCell(formatAmount(paySlip.getOtherTaxedAmount()), false));

        // Net Salary
        table.addCell(createCell("Net Salary", true));
        table.addCell(createCell(formatAmount(paySlip.getNetSalary()), true));

        document.add(table);
    }

    private void addFooter(Document document, PaySlip paySlip) {
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Generated on: " + DateTimeFormatter.ofPattern("dd MMMM yyyy")
                .format(java.time.LocalDate.now()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10));
    }

    private Cell createCell(String content, boolean isBold) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (isBold) {
            cell.setBold();
        }
        return cell;
    }

    private String formatAmount(java.math.BigDecimal amount) {
        return String.format(Locale.US, "%,.2f RWF", amount);
    }
} 