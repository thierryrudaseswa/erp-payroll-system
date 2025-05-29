package rw.gov.erp.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Parse string to LocalDate
     * @param dateStr Date in format yyyy-MM-dd
     * @return LocalDate object
     * @throws DateTimeParseException if the date string is invalid
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * Format LocalDate to string
     * @param date LocalDate to format
     * @return Formatted date string in yyyy-MM-dd format
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Check if a string is a valid date
     * @param dateStr Date string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Calculate age from date of birth
     * @param dateOfBirth Date of birth
     * @return Age in years
     */
    public static int calculateAge(LocalDate dateOfBirth) {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    /**
     * Check if person is adult (18 years or older)
     * @param dateOfBirth Date of birth
     * @return true if adult, false otherwise
     */
    public static boolean isAdult(LocalDate dateOfBirth) {
        return calculateAge(dateOfBirth) >= 18;
    }
} 