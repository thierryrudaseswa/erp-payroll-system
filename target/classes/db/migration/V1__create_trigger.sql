CREATE OR REPLACE FUNCTION generate_payment_message()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'PAID' AND OLD.status = 'PENDING' THEN
        INSERT INTO messages (
            id,
            employee_code,
            message_content,
            month,
            year,
            sent_at,
            sent
        ) VALUES (
            gen_random_uuid(),
            NEW.employee_code,
            format(
                'Dear %s, Your salary of %s/%s from Government of Rwanda %s has been credited to your %s account successfully.',
                (SELECT first_name FROM employees WHERE code = NEW.employee_code),
                CASE NEW.month
                    WHEN 1 THEN 'January'
                    WHEN 2 THEN 'February'
                    WHEN 3 THEN 'March'
                    WHEN 4 THEN 'April'
                    WHEN 5 THEN 'May'
                    WHEN 6 THEN 'June'
                    WHEN 7 THEN 'July'
                    WHEN 8 THEN 'August'
                    WHEN 9 THEN 'September'
                    WHEN 10 THEN 'October'
                    WHEN 11 THEN 'November'
                    WHEN 12 THEN 'December'
                END,
                NEW.year,
                NEW.net_salary,
                NEW.employee_code
            ),
            NEW.month,
            NEW.year,
            CURRENT_TIMESTAMP,
            false
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER pay_slip_approved_trigger
    AFTER UPDATE ON pay_slips
    FOR EACH ROW
    EXECUTE FUNCTION generate_payment_message(); 