package rw.gov.erp.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException employeeNotFound(String code) {
        return new ResourceNotFoundException("Employee not found with code: " + code);
    }

    public static ResourceNotFoundException employmentNotFound(String code) {
        return new ResourceNotFoundException("Employment not found with code: " + code);
    }

    public static ResourceNotFoundException paySlipNotFound(String id) {
        return new ResourceNotFoundException("PaySlip not found with id: " + id);
    }
} 