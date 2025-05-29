package rw.gov.erp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rw.gov.erp.repository.PaySlipRepository;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final PaySlipRepository paySlipRepository;

    public boolean isCurrentUser(String employeeCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getCode().equals(employeeCode);
    }

    public boolean isCurrentUserPaySlip(String paySlipId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return paySlipRepository.findById(paySlipId)
                .map(paySlip -> paySlip.getEmployee().getCode().equals(userPrincipal.getCode()))
                .orElse(false);
    }
} 