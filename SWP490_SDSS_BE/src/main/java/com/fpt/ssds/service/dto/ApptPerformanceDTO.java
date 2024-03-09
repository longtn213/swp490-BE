package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApptPerformanceDTO {
    private Long branchId;
    private String branchName;
    private String branchCode;
    private ApptPerformanceByBranchDTO performanceByBranch;
}
