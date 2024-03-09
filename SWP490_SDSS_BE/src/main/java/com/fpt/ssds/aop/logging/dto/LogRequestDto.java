package com.fpt.ssds.aop.logging.dto;

import lombok.Data;

@Data
public class LogRequestDto {
    private String requestId;

    private String method;

    private String uri;

    private String query;

    private String[] header;

    private String body;

    private String code;

    private long latency;

    private Object responseData;

    private int httpStatusCode;
}
