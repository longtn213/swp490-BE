package com.fpt.ssds.common.exception;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SSDSBusinessException extends SSDSRuntimeException{
    private String code;
    private String message;
    private List<?> data;
    private List<?> fieldErrorDTOS;

    public SSDSBusinessException(String code) {
        this.code = code;
    }

    public SSDSBusinessException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public SSDSBusinessException(String code, List<?> data) {
        this.code = code;
        this.data = data;
    }

    public SSDSBusinessException(String code, List<?> data, List<?> fieldErrorDTOS) {
        this.code = code;
        this.data = data;
        this.fieldErrorDTOS = fieldErrorDTOS;
    }
}
