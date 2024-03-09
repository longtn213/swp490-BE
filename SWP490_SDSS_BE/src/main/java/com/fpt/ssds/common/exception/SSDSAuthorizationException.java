package com.fpt.ssds.common.exception;

public class SSDSAuthorizationException extends SSDSProcessException {

    public SSDSAuthorizationException(String code) {
        super(code);
    }

    public SSDSAuthorizationException(String code, String message) {
        super(code, message);
    }
}
