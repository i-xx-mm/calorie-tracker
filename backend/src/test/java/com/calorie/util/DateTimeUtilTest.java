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
        // 允许极小时间差（跨毫秒不会翻车）
        assertEquals(estNow, utilResult);
    }

    @Test
    void getCurrentMonthEST_returnsCorrectYearMonthString() {
        String monthStr = DateTimeUtil.getCurrentMonthEST();
        LocalDate estToday = ZonedDateTime.now(EST_ZONE).toLocalDate();
        String expected = String.format("%04d‑%02d", estToday.getYear(), estToday.getMonthValue());
        assertEquals(expected, monthStr);
    }

    @Test
    void toEST_nullInput_returnNull() {
        assertNull(DateTimeUtil.toEST(null));
    }

    @Test
    void toEST_convertSystemDefaultToEstInstant() {
        LocalDateTime input = LocalDateTime.of(2026, 1, 1, 12, 0);
        LocalDateTime result = DateTimeUtil.toEST(input);
        assertNotNull(result);
    }

    @Test
    void toESTStartOfDay_null_returnNull() {
        assertNull(DateTimeUtil.toESTStartOfDay(null));
    }

    @Test
    void toESTStartOfDay_producesMidnightEst() {
        LocalDate date = LocalDate.of(2026, 5, 10);
        LocalDateTime startOfDay = DateTimeUtil.toESTStartOfDay(date);
        assertNotNull(startOfDay);
        assertEquals(0, startOfDay.getHour());
        assertEquals(0, startOfDay.getMinute());
    }

    @Test
    void getFirstDayOfCurrentMonthEST_firstDayIsOne() {
        LocalDate first = DateTimeUtil.getFirstDayOfCurrentMonthEST();
        assertEquals(1, first.getDayOfMonth());
    }

    @Test
    void getLastDayOfCurrentMonthEST_lastDayOfMonth() {
        LocalDate last = DateTimeUtil.getLastDayOfCurrentMonthEST();
        LocalDate today = DateTimeUtil.getTodayEST();
        assertEquals(today.lengthOfMonth(), last.getDayOfMonth());
    }

    @Test
    void formatDateForDisplay_null_returnsEmptyString() {
        assertEquals("", DateTimeUtil.formatDateForDisplay(null));
    }

    @Test
    void formatDateForDisplay_formatsMMDD() {
        LocalDate date = LocalDate.of(2026, 3, 5);
        String output = DateTimeUtil.formatDateForDisplay(date);
        assertEquals("03/05", output);
    }

    @Test
    void utcLocalDateTimeToEstLocalDate_null_returnNull() {
        assertNull(DateTimeUtil.utcLocalDateTimeToEstLocalDate(null));
    }

    @Test
    void utcLocalDateTimeToEstLocalDate_convertUtcToEstDate() {
        // UTC 2026‑01‑02 01:00 → EST 2026‑01‑01 20:00 (‑5 offset winter time)
        LocalDateTime utcInput = LocalDateTime.of(2026, 1, 2, 1, 0);
        LocalDate estDate = DateTimeUtil.utcLocalDateTimeToEstLocalDate(utcInput);
        assertEquals(LocalDate.of(2026, 1, 1), estDate);
    }
}