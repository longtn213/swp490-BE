package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.RolePermissionImportDTO;

import java.util.List;

public interface RoleService {
    void importRolePermission(List<RolePermissionImportDTO> rolePermissionImportDTOS);
}
