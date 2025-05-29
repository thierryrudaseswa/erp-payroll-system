-- Insert default deductions
INSERT INTO deductions (code, deduction_name, percentage) VALUES
    (gen_random_uuid(), 'EmployeeTax', 30.0),
    (gen_random_uuid(), 'Pension', 6.0),
    (gen_random_uuid(), 'MedicalInsurance', 5.0),
    (gen_random_uuid(), 'Others', 5.0); 