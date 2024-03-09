package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.SpaService;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for the {@link SpaService} entity
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpaServiceDto implements Serializable {
    private Long id;
    @NotEmpty(message = "Không được để trống tên dịch vụ")
    private String name;
    private String code;
    private String description;
    @Min(value = 0, message = "Thời gian thực hiện không thể là số âm")
    @NotNull(message = "Không được để trống thời gian thực hiện")
    private Long duration;
    private Boolean isActive;
    private Long categoryId;
    private String categoryName;
    private String categoryCode;
    private Long equipmentTypeId;
    private String equipmentName;
    @Min(value = 0, message = "Giá tiền không thể là số âm")
    @NotNull(message = "Không được để trống giá")
    private Double currentPrice;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private List<FileDto> files = new ArrayList<>();
}
