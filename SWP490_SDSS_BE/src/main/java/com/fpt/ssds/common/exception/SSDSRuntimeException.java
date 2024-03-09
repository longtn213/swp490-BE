package com.fpt.ssds.common.exception;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SSDSRuntimeException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;

    public SSDSRuntimeException(String code) {
        this.code = code;
    }
}
