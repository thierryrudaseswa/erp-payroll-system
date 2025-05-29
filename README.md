  +-----------------------+
|  Controller Layer     | -> REST APIs (Spring Web)
+-----------------------+
|  Service Layer        | -> Business Logic
+-----------------------+
|  Repository Layer     | -> Data Access (Spring Data JPA)
+-----------------------+
|  Entity/Model Layer   | -> POJOs with JPA annotations
+-----------------------+
|  Security Layer       | -> JWT Authentication & Role-based Authorization
+-----------------------+
|  Messaging Layer      | -> Email notifications (JavaMailSender)

+-----------------------+

-- 1. Delete from messages table
DELETE FROM messages WHERE employee_code = '6beaf635-c37a-4e69-be17-6a79a7958671';

-- 2. Delete from pay_slips table
DELETE FROM pay_slips WHERE employee_code = '6beaf635-c37a-4e69-be17-6a79a7958671';

-- 3. Delete from employments table
DELETE FROM employments WHERE employee_code = '6beaf635-c37a-4e69-be17-6a79a7958671';

-- 4. Delete from employee_roles table (the one causing the error)
DELETE FROM employee_roles WHERE employee_code = '6beaf635-c37a-4e69-be17-6a79a7958671';

-- 5. Finally, delete from employees table
DELETE FROM employees WHERE email = 'trudaseswa@gmail.com';



Technology	Reason
Spring Boot	Rapid development, auto-configuration, production-ready
Spring Data JPA	Simplifies database operations with repositories and query derivation
Spring Security + JWT	Stateless authentication and role-based access
PostgreSQL	Widely used RDBMS, ideal for structured payroll data
Swagger (Springdoc/OpenAPI)	API documentation, test endpoints, developer-friendly

JavaMailSender	For sending payslip notifications via email
Lombok	Reduces boilerplate in POJOs (getters/setters, constructors)
Postman	Manual testing of API endpoints



list of POJOs


1. Employee.java
java
Copy
Edit
public class Employee {
    private UUID code;
    private LocalDate dateOfBirth;
    private String email;
    private String firstName;
    private String lastName;
    private String mobile;
    private String password;
    private String status;
}
2. Employment.java
java
Copy
Edit
public class Employment {
    private String code;
    private BigDecimal baseSalary;
    private String department;
    private LocalDate joiningDate;
    private String position;
    private String status;
    private UUID employeeCode;
}
3. Deduction.java
java
Copy
Edit
public class Deduction {
    private UUID code;
    private String deductionName;
    private int percentage;
}
4. PaySlip.java
java
Copy
Edit
public class PaySlip {
    private UUID id;
    private BigDecimal employeeTaxedAmount;
    private BigDecimal grossSalary;
    private BigDecimal houseAmount;
    private BigDecimal medicalInsuranceAmount;
    private int month;
    private BigDecimal netSalary;
    private BigDecimal otherTaxedAmount;
    private BigDecimal pensionAmount;
    private String status;
    private BigDecimal transportAmount;
    private int year;
    private UUID employeeCode;
}
5. Message.java
java
Copy
Edit
public class Message {
    private UUID id;
    private String messageContent;
    private int month;
    private boolean sent;
    private LocalDateTime sentAt;
    private int year;
    private UUID employeeCode;
}
6. EmployeeRole.java (based on your error earlier)
java
Copy
Edit
public class EmployeeRole {
    private UUID id;
    private String role;
    private UUID employeeCode;
}

