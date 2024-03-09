package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.Session;
import lombok.Data;

import java.time.Instant;

/**
 * A DTO for the {@link Session} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionDto {

    private Long id;

    private Integer order;

    private Instant expectedStartTime;

    private Long spaCourseId;

    private Long appointmentMasterId;

    private SpaServiceDto spaService;

    private LookupDto status;
}
