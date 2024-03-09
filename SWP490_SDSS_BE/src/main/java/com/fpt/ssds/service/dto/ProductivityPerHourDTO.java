package com.fpt.ssds.service.dto;

import lombok.Data;

@Data
public class ProductivityPerHourDTO {
    private Integer hour;
    private Integer totalReady;
    private Integer totalInprocess;
    private Integer totalClosed;
}
