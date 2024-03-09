package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.ReasonMessage;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link ReasonMessage} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReasonMessageDto {
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
    private Long id;
    private String title;
    private String code;
    private String description;
    private Boolean isActive;
    private Boolean requireNote;
    private ReasonMessageTypeDto reasonMessageType;
}
