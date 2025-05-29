package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.entity.Message;
import rw.gov.erp.entity.PaySlip;
import rw.gov.erp.repository.MessageRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final JavaMailSender emailSender;
    private static final String INSTITUTION = "Government of Rwanda";

    @Transactional
    public void createPaymentMessage(PaySlip paySlip) {
        Message message = new Message();
        message.setEmployee(paySlip.getEmployee());
        message.setMonth(paySlip.getMonth());
        message.setYear(paySlip.getYear());
        message.setSentAt(LocalDateTime.now());
        
        String messageContent = String.format(
            "Dear %s, Your salary of %s/%d from %s %s has been credited to your %s account successfully.",
            paySlip.getEmployee().getFirstName(),
            getMonthName(paySlip.getMonth()),
            paySlip.getYear(),
            INSTITUTION,
            paySlip.getNetSalary(),
            paySlip.getEmployee().getCode()
        );
        
        message.setMessageContent(messageContent);
        messageRepository.save(message);
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void sendPendingMessages() {
        List<Message> pendingMessages = messageRepository.findBySentFalse();
        
        pendingMessages.forEach(message -> {
            try {
                SimpleMailMessage emailMessage = new SimpleMailMessage();
                emailMessage.setTo(message.getEmployee().getEmail());
                emailMessage.setSubject("Salary Payment Notification");
                emailMessage.setText(message.getMessageContent());
                
                emailSender.send(emailMessage);
                
                message.setSent(true);
                messageRepository.save(message);
            } catch (Exception e) {
                // Log error but don't mark as sent
                e.printStackTrace();
            }
        });
    }

    private String getMonthName(int month) {
        return DateTimeFormatter.ofPattern("MMMM")
                .format(LocalDateTime.of(2024, month, 1, 0, 0));
    }
} 