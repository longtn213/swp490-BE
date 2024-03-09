package com.fpt.ssds.service.dto;

import lombok.Data;

@Data
public class RevenueByServiceDTO {
    private String serviceName;
    private Integer totalCompletedApptService;
    private Double total;
    private Double totalPayAmount;
}
