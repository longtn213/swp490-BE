package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link com.fpt.ssds.domain.Category} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDto implements Serializable {
    private Long id;

    @NotNull(message = "Không được để trống tên loại dịch vụ")
    private String name;

    private String code;

    private String description;

    private List<SpaServiceDto> spaServices;
}
