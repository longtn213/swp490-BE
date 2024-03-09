package com.fpt.ssds.common.exception;

public class TokenException extends SSDSProcessException {
    public TokenException(String code) {
        super(code);
    }

    public TokenException(String code, String message) {
        super(code, message);
    }
}
