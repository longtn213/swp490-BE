package com.fpt.ssds.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.ssds.common.exception.SSDSRuntimeException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.service.dto.FieldErrorDTO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static final String ISO_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    /**
     * convertObjectToJsonString
     *
     * @param o
     * @return
     */
    public static <T> String convertObjectToJsonString(T o) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T convertJsonStringToObject(final String json, final Class<T> oClass) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // dung de accept neu oject khai bao khong dung dinh dang
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            return mapper.readValue(json, oClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> splitString(String input, String regex) {
        if (org.springframework.util.StringUtils.isEmpty(input)) {
            return new ArrayList<>();
        } else {
            String[] arr = input.split(regex);
            List<String> list = Arrays.asList(arr);
            return list;
        }
    }

    public static boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String generateCode(String prefix) {
        StringBuilder code = new StringBuilder(prefix + "_");
        code.append((new Date()).getTime() + "_");
        code.append(String.format("%03d", Utils.getRandomNumber(0, 999)));
        return code.toString();
    }


    public static String getChildPath(List<String> paths, int index) {
        String childPath = "";
        for (int size = paths.size(); index < size; index++) {
            childPath += paths.get(index) + ".";
        }
        return childPath.substring(0, childPath.length() - 1);
    }

    /*public static String getTimezone(User user, String systemTimezone) {
        String timezone = user.getTimezone();
        return org.apache.commons.lang3.StringUtils.isEmpty(timezone) ? systemTimezone : timezone;
    }*/

    public static List<Long> mergeListLong(List<List<Long>> lst) {
        List<Long> result = new ArrayList<>();
        lst.forEach(longs -> {
            result.addAll(longs);
        });
        return result;
    }

    public static Integer getIntegerValue(Integer value) {
        return Objects.nonNull(value) ? value : 0;
    }

    public static long getLongValue(Long value) {
        return Objects.nonNull(value) ? value : 0;
    }

    public static Double getDoubleValue(Double value) {
        return Objects.nonNull(value) ? value : 0;
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static String genCodeFromName(String name) {
        return Utils.deAccent(name).toUpperCase().replace(" ", "_");
    }

    public static int getSecondFromString(String timeString) {
        if (!timeString.matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$")) {
            throw new SSDSRuntimeException(ErrorConstants.INVALID_TIME_FORMAT);
        }
        PeriodFormatter pf = new PeriodFormatterBuilder().
            appendHours().appendSeparator(":").
            appendMinutes().appendSeparator(":").
            appendSeconds().toFormatter();
        Period period = pf.parsePeriod(timeString);
        return period.toStandardSeconds().getSeconds();
    }

    public static Boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber.matches("(84|0[3|5|7|8|9])+([0-9]{8})\\b");
    }
}
