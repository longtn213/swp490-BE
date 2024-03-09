package com.fpt.ssds.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.fpt.ssds.constant.Constants.REQUEST_HEADER.AUTHORIZATION_HEADER;

public class HTTPUtils {

    public static String getAttributeAsString(HttpServletRequest request, String key) {
        return (String) request.getAttribute(key);
    }

    public static Object getAttribute(HttpServletRequest request, String key) {
        return request.getAttribute(key);
    }

    public static int getAttributeAsInt(HttpServletRequest request, String key) {
        Object data = request.getAttribute(key);
        if (data == null) {
            return 0;
        }
        if (data instanceof Integer) {
            return (Integer) data;
        }
        return 0;
    }

    public static long getAttributeAsLong(HttpServletRequest request, String key) {
        Object data = request.getAttribute(key);
        if (data == null) {
            return 0;
        }
        if (data instanceof Long) {
            return (Long) data;
        }
        return 0;
    }

    public static String getBody(String body) {
        if (body != null) {
            return body;
        }
        return "";
    }

    public static void setAttribute(HttpServletRequest request, String key, String value) {
        request.setAttribute(key, value);
    }

    public static String getRequestHeaderAsString(HttpServletRequest request, String key) {
        return (String) request.getHeader(key);
    }

    public static Object getRequestHeader(HttpServletRequest request, String key) {
        return request.getHeader(key);
    }

    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
