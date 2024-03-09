package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * A DTO for the {@link com.fpt.ssds.domain.Role} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDto {
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("isActive")
    private Boolean isActive;

    private List<PermissionDTO> permissions = new ArrayList<>();
}
