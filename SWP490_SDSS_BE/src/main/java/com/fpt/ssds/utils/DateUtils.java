package com.fpt.ssds.utils;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
public class DateUtils {

    public static final String STR_DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String STR_DEFAULT_DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";
    public static final String STR_DEFAULT_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String TIME_DATE_FORMAT = "HH:mm dd/MM/yyyy";
    public static final String STR_DEFAULT_TIME_SECOND_FORMAT = "dd/MM/yyyy HH:mm:ss.SSS";
    public static final String STR_DATE_FORMAT = "dd/MM/yyyy";
    public static final String STR_TIME_WITH_TIMEZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String STR_TIME_WITH_TIMEZONE_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String STR_DEFAULT_DATE_FORMAT2 = "dd/MM/yyyy";
    public static final String STR_ISO_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";

    public static Instant atStartOfDay(Instant date, String zone) {
        LocalDateTime localDateTime = dateToLocalDateTime(date, zone);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay, zone);
    }

    public static Instant atEndOfDay(Instant date, String zone) {
        LocalDateTime localDateTime = dateToLocalDateTime(date, zone);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay, zone);
    }

    public static LocalDateTime dateToLocalDateTime(Instant date, String zone) {
        return LocalDateTime.ofInstant(date, ZoneId.of(zone));
    }

    private static Instant localDateTimeToDate(LocalDateTime localDateTime, String zone) {
        log.info("DateUtils.stringToInstantWithZone toDate: {} {} {} {}", localDateTime, zone, localDateTime.atZone(ZoneId.of(zone)), localDateTime.atZone(ZoneId.of(zone)).toInstant());
        return localDateTime.atZone(ZoneId.of(zone)).toInstant();
    }

    public static Instant getInstantByDayAndSecond(Instant instant, int second, String zone) {
        Instant result = atStartOfDay(instant, zone);
        return result.plus(second, ChronoUnit.SECONDS);
    }

    public static String formatInstantToString(Instant instant, String zone, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.of(zone));
        return dateTimeFormatter.format(instant);
    }

    public static String formatLocalDateTimeToString(LocalDateTime date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public static ZonedDateTime stringToZonedDateTime(String timeString) {
        try {
            DateTimeFormatter formatterZ = DateTimeFormatter.ofPattern(DateUtils.STR_TIME_WITH_TIMEZONE_FORMAT);
            ZonedDateTime formatDateTimeZ = ZonedDateTime.parse(timeString, formatterZ);
            return formatDateTimeZ;
        } catch (Exception e) {
            log.error("DateUtils.stringToZonedDateTime:" + e.getMessage());
            return null;
        }
    }

    public static Instant stringTimeWithZoneToDate(String timeString) {
        try {
            return stringToZonedDateTime(timeString).toInstant();
        } catch (Exception e) {
            log.error("DateUtils.stringTimeWithZoneToDate:" + e.getMessage());
            return null;
        }
    }

    public static Date addSecond(final Date date, final int second) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second); //second number would decrement the days
        return cal.getTime();
    }

    public static Map<String, Instant> zonedDateTimeToInstant(ZonedDateTime startDate, ZonedDateTime endDate, String defaultTimeZone) {
        Map<String, Instant> instantMap = new HashMap<>();
        Instant startTime;
        Instant endTime;

        if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
            Instant now = ZonedDateTime.now().toInstant();
            startTime = atStartOfDay(now, defaultTimeZone);
            endTime = atEndOfDay(now, defaultTimeZone);
        } else if (Objects.nonNull(startDate) && Objects.isNull(endDate)) {
            startTime = startDate.toInstant();
            endTime = atEndOfDay(startTime, TimeZone.getTimeZone(startDate.getZone()).getID());
        } else if (Objects.isNull(startDate) && Objects.nonNull(endDate)) {
            endTime = endDate.toInstant();
            startTime = atStartOfDay(endTime, TimeZone.getTimeZone(endDate.getZone()).getID());
        } else {
            startTime = startDate.toInstant();
            endTime = endDate.toInstant();
        }

        if (startTime.isAfter(endTime)) {
            throw new SSDSBusinessException(ErrorConstants.START_TIME_DOES_NOT_GREATER_THAN_END_TIME);
        }
        instantMap.put("startTime", startTime);
        instantMap.put("endTime", endTime);
        return instantMap;
    }

    public static Instant getStartOfLastMonth(Instant instant, String defaultTimeZone) {
        ZoneId zone = ZoneId.of(defaultTimeZone);
        LocalDate date = LocalDate.ofInstant(instant, zone);
        LocalDate localDate = date.minus(1, ChronoUnit.MONTHS);
        return localDate.withDayOfMonth(1).atStartOfDay(zone).toInstant();
    }

    public static Instant getLastOfLastMonth(Instant instant, String defaultTimeZone) {
        ZoneId zone = ZoneId.of(defaultTimeZone);
        LocalDate currentDate = LocalDate.ofInstant(instant, zone);
        LocalDate localDate = currentDate.minus(1, ChronoUnit.MONTHS);

        Date date = Date.from(localDate.atStartOfDay(zone).toInstant());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);


        return localDate.withDayOfMonth(calendar.getActualMaximum(Calendar.DATE)).atTime(23, 59, 59).atZone(zone).toInstant();
    }
}
