package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.Role;
import com.fpt.ssds.service.dto.RoleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper extends EntityMapper<RoleDto, Role> {
    default Role fromId(Long id) {
        if (id == null) {
            return null;
        }
        Role role = new Role();
        role.setId(id);
        return role;
    }
}
