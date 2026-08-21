package com.calorie.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Utility class for handling EST timezone consistently across the application
 * Ensures all dates are in EST (America/New_York) timezone for consistency
 */
public class DateTimeUtil {
    private static final ZoneId EST_ZONE = ZoneId.of("America/New_York");

    /**
     * Get today's date in EST timezone
     * Returns YYYY‑MM‑DD format
     */
    public static LocalDate getTodayEST() {
        ZonedDateTime estNow = ZonedDateTime.now(EST_ZONE);
        return estNow.toLocalDate();
    }

    /**
     * Get current month in EST timezone
     * Returns YYYY‑MM format
     */
    public static String getCurrentMonthEST() {
        LocalDate today = getTodayEST();
        return String.format("%04d‑%02d", today.getYear(), today.getMonthValue());
    }

    /**
     * Convert a LocalDateTime to EST timezone
     */
    public static LocalDateTime toEST(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        ZonedDateTime zdt = dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(EST_ZONE);
        return zdt.toLocalDateTime();
    }

    /**
     * Convert a LocalDate to start of day in EST timezone
     */
    public static LocalDateTime toESTStartOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(EST_ZONE).toLocalDateTime();
    }

    /**
     * Get the start date of the current month in EST
     */
    public static LocalDate getFirstDayOfCurrentMonthEST() {
        LocalDate today = getTodayEST();
        return today.withDayOfMonth(1);
    }

    /**
     * Get the end date of the current month in EST
     */
    public static LocalDate getLastDayOfCurrentMonthEST() {
        LocalDate today = getTodayEST();
        return today.withDayOfMonth(today.lengthOfMonth());
    }

    /**
     * Format a LocalDate as MM/DD for display (EST context)
     */
    public static String formatDateForDisplay(LocalDate date) {
        if (date == null) {
            return "";
        }
        return String.format("%02d/%02d", date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * UTC‑LocalDateTime (stored in MongoDB) → EST(America/New_York) LocalDate for frontend DTO
     */
    public static LocalDate utcLocalDateTimeToEstLocalDate(LocalDateTime utcDateTime) {
        if (utcDateTime == null) {
            return null;
        }
        ZonedDateTime utcZoned = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime estZoned = utcZoned.withZoneSameInstant(EST_ZONE);
        return estZoned.toLocalDate();
    }
}