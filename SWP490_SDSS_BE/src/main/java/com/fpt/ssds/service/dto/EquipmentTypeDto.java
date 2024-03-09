package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.EquipmentType;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link EquipmentType} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentTypeDto implements Serializable {
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
    private Long id;
    @NotNull(message = "Không được để trống tên loại thiết bị")
    @NotEmpty(message = "Không được để trống tên loại thiết bị")
    private String name;
    private String code;
    private String description;
}
