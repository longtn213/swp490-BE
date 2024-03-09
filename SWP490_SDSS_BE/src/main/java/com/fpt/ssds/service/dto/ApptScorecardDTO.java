package com.fpt.ssds.service.dto;

import lombok.Data;

@Data
public class ApptScorecardDTO {
    private Integer totalWaitForConfirm;
    private Integer totalReady;
    private Integer totalInprocess;
    private Integer totalClosed;
    private Integer totalCanceled;
    private Integer totalOverdue;
}
