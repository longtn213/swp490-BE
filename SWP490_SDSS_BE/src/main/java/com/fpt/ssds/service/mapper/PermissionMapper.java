package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.Permission;
import com.fpt.ssds.service.dto.PermissionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface PermissionMapper extends EntityMapper<PermissionDTO, Permission> {
    PermissionDTO toDto(Permission permission);

    Permission toEntity(PermissionDTO permissionDTO);

    default Permission fromId(Long id) {
        if (id == null) {
            return null;
        }
        Permission permission = new Permission();
        permission.setId(id);
        return permission;
    }
}
