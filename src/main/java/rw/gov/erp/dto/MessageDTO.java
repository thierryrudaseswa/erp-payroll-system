package rw.gov.erp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private String id;
    private String employeeCode;
    private String messageContent;
    private Integer month;
    private Integer year;
    private LocalDateTime sentAt;
    private boolean sent;
} 