package com.velaris.shared.util;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@UtilityClass
public class DateTimeUtils {

    public static final ZoneId DEFAULT_ZONE = ZoneOffset.UTC;

    public static Date toDate(Instant instant) {
        return Date.from(instant);
    }

    public static Instant toInstant(Date date) {
        return date.toInstant();
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(DEFAULT_ZONE).toLocalDate();
    }

    public static String formatInstant(Instant instant, String pattern) {
        return DateTimeFormatter.ofPattern(pattern)
                .withZone(DEFAULT_ZONE)
                .format(instant);
    }

    public static Instant now() {
        return Instant.now();
    }

    public static Instant plusMillis(Instant instant, long millis) {
        return instant.plusMillis(millis);
    }

    public static boolean isFuture(Instant instant) {
        return instant.isAfter(now());
    }

    public static boolean isPast(Instant instant) {
        return instant.isBefore(now());
    }
}
