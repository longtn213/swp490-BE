package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolePermissionImportDTO {
    private String roleCode;
    @NotNull(message = "Không được để trống mã quyền")
    private String permissionCode;
    private Boolean isActive;
    private String permissionRole;
}
