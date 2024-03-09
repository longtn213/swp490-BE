package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fpt.ssds.domain.AppointmentService;
import com.fpt.ssds.domain.User;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * A DTO for the {@link AppointmentService} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentServiceDto {
    private Long id;

    private Instant expectedStartTime;

    private Instant expectedEndTime;

    private Instant actualStartTime;

    private Instant actualEndTime;

    private Instant cancelTime;

    private String cancelBy;

    private Double total;

    private Double payAmount;

    @NotNull(message = "Không được để trống thông tin dịch vụ spa")
    private Long spaServiceId;

    private Double spaServicePrice;

    private String spaServiceCode;

    private String spaServiceName;
    
    private Long duration;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private ReasonMessageDto canceledReason;

    private String note;

    private Integer order;

    private UserListingDTO specialist;

    private String specialistInfoNote;
}
