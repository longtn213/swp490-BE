package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApptPerformanceMetrixDTO {
    private Double value = 0D;
    private Double oldValue = 0D;
    private Double increment = 0D;
    private Double chainRatio = 0D;
    private List<PointDTO> points;
}
