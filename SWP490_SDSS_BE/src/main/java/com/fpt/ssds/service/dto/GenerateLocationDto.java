package com.fpt.ssds.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenerateLocationDto {
    private List<String> objectName;
    private String parentCode;
    private String locationType;
}
