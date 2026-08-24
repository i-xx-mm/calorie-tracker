package com.calorie.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilTest {

    private static final ZoneId EST_ZONE = ZoneId.of("America/New_York");

    @Test
    void getTodayEST_returnsEstLocalDate() {
        LocalDate utilResult = DateTimeUtil.getTodayEST();
        LocalDate estNow = ZonedDateTime.now(EST_ZONE).toLocalDate();

        assertEquals(estNow, utilResult);
    }

    @Test
    void utcLocalDateTimeToEstLocalDate_null_returnNull() {
        assertNull(DateTimeUtil.utcLocalDateTimeToEstLocalDate(null));
    }

    @Test
    void utcLocalDateTimeToEstLocalDate_convertUtcToEstDate() {
        // UTC 2026‑01‑02 01:00 -> EST 2026‑01‑01 20:00 (‑5 offset winter time)
        LocalDateTime utcInput = LocalDateTime.of(2026, 1, 2, 1, 0);
        LocalDate estDate = DateTimeUtil.utcLocalDateTimeToEstLocalDate(utcInput);
        assertEquals(LocalDate.of(2026, 1, 1), estDate);
    }
    @Test
    void getUTCRange_convertNyLocalDateToUtcTimeRange_winterTime() {
        // NY local date 2026‑01‑01 00:00 (EST UTC‑5) → UTC 2026‑01‑01 05:00
        // Next‑day NY 00:00 -> UTC 2026‑01‑02 05:00
        LocalDate nyDate = LocalDate.of(2026, 1, 1);

        LocalDateTime[] utcRange = DateTimeUtil.getUtcRange(nyDate);

        LocalDateTime expectedStartUtc = LocalDateTime.of(2026, 1, 1, 5, 0);
        LocalDateTime expectedEndUtc = LocalDateTime.of(2026, 1, 2, 5, 0);

        assertEquals(expectedStartUtc, utcRange[0]);
        assertEquals(expectedEndUtc, utcRange[1]);
    }

    @Test
    void getUTCRange_convertNyLocalDateToUtcTimeRange_daylightSaving() {
        // Summer EDT UTC‑4
        // NY local 2026‑07‑10 00:00 -> UTC 2026‑07‑10 04:00
        // Next‑day NY 00:00 -> UTC 2026‑07‑11 04:00
        LocalDate nyDate = LocalDate.of(2026, 7, 10);

        LocalDateTime[] utcRange = DateTimeUtil.getUtcRange(nyDate);

        LocalDateTime expectedStartUtc = LocalDateTime.of(2026, 7, 10, 4, 0);
        LocalDateTime expectedEndUtc = LocalDateTime.of(2026, 7, 11, 4, 0);

        assertEquals(expectedStartUtc, utcRange[0]);
        assertEquals(expectedEndUtc, utcRange[1]);
    }
}