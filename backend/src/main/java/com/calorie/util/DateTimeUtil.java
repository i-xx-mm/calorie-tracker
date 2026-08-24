package com.calorie.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Utility class for handling EST timezone consistently across the application
 * All timestamps persisted inside MongoDB are stored as UTC LocalDateTime
 * Converts between stored-UTC and New-York local calendar date/time for frontend DTO and database range queries
 */
public class DateTimeUtil {
    /**
     * Time zone constant for New_York. automatically handles EST winter/summer daylight saving switch
     */
    private static final ZoneId EST_ZONE = ZoneId.of("America/New_York");

    /**
     * Get today's date in EST timezone
     *
     * @return YYYY-MM-DD format
     */
    public static LocalDate getTodayEST() {
        ZonedDateTime estNow = ZonedDateTime.now(EST_ZONE);
        return estNow.toLocalDate();
    }

    /**
     * UTC-LocalDateTime (stored in MongoDB) -> EST(America/New_York) LocalDate for frontend DTO
     *
     * @param utcDateTime local date in UTC time zone
     */
    public static LocalDate utcLocalDateTimeToEstLocalDate(LocalDateTime utcDateTime) {
        if (utcDateTime == null) {
            return null;
        }
        ZonedDateTime utcZoned = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime estZoned = utcZoned.withZoneSameInstant(EST_ZONE);
        return estZoned.toLocalDate();
    }

    /**
     * EST(America/New_York) LocalDate -> UTC-LocalDateTime start/end range for MongoDB query
     * @param date local date in America/New_York time zone
     * @return array [startUtcLocalDateTime, endUtcLocalDateTime]
     */
    public static LocalDateTime[] getUtcRange(LocalDate date) {
        ZonedDateTime nyStart = date.atStartOfDay(EST_ZONE);
        ZonedDateTime nyEnd = date.plusDays(1)
                .atStartOfDay(EST_ZONE);

        return new LocalDateTime[]{
                nyStart.withZoneSameInstant(
                        ZoneId.of("UTC")).toLocalDateTime(),
                nyEnd.withZoneSameInstant(
                        ZoneId.of("UTC")).toLocalDateTime()
        };
    }
}