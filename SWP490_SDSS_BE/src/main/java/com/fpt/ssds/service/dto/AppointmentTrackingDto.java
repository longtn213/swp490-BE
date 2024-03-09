package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.AppointmentTracking;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link AppointmentTracking} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentTrackingDto implements Serializable {
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
    private Long id;
    private Instant time;
    private Long maxQty;
    private Long bookedQty;
    private Boolean isAvailable;
    private Boolean isFirstTimeInDay;
    private Boolean isLastTimeInDay;
    private Long branchId;
    private String branchName;
}
