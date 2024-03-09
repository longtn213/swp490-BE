package com.fpt.ssds.common.handler.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestBodyData {
    private List<Object> parameters;
}
