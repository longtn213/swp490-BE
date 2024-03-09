package com.fpt.ssds.common.exception;

public class SSDSProcessException extends SSDSRuntimeException {
    private static final long serialVersionUID = 1L;

    public SSDSProcessException(String code) {
        super(code);
    }

    public SSDSProcessException(String code, String message) {
        super(code, message);
    }
}
