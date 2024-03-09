package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.Permission;
import com.fpt.ssds.domain.Role;
import com.fpt.ssds.repository.PermissionRepository;
import com.fpt.ssds.repository.RoleRepository;
import com.fpt.ssds.service.RoleService;
import com.fpt.ssds.service.dto.FieldErrorDTO;
import com.fpt.ssds.service.dto.RoleDto;
import com.fpt.ssds.service.dto.RolePermissionImportDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    private final MessageSource messageSource;

    @Override
    @Transactional
    public void importRolePermission(List<RolePermissionImportDTO> rolePermissionImportDTOS) {
        Set<String> permissionRoleSet = new HashSet<>();
        Set<String> permissionSet = new HashSet<>();
        Set<String> roleSet = new HashSet<>();
        for (RolePermissionImportDTO rolePermissionImportDTO : rolePermissionImportDTOS) {
            String permissionRole = rolePermissionImportDTO.getPermissionCode() + "_" + rolePermissionImportDTO.getRoleCode();
            permissionRoleSet.add(permissionRole);
            permissionSet.add(rolePermissionImportDTO.getPermissionCode());
            roleSet.add(rolePermissionImportDTO.getRoleCode());
        }

        List<Role> roles = roleRepository.findByCodeIn(roleSet);
        List<Permission> permissions = permissionRepository.findByCodeIn(permissionSet);
        validateRoles(rolePermissionImportDTOS, roles, roleSet, permissionRoleSet);

        Map<String, Role> roleMap = new HashMap<>();
        for (Role role : roles) {
            roleMap.put(role.getCode(), role);
        }

        Set<String> newPermissionCode = new HashSet<>(permissionSet);
        newPermissionCode.removeAll(permissions.stream().map(Permission::getCode).collect(Collectors.toList()));
        createNewPermissions(newPermissionCode, roleMap, rolePermissionImportDTOS);

        permissionSet.removeAll(newPermissionCode);
        if (CollectionUtils.isNotEmpty(permissionSet)) {
            updateRolePermission(rolePermissionImportDTOS, roleMap, permissions);
        }
    }

    private void validateRoles(List<RolePermissionImportDTO> rolePermissionImportDTOS,
                               List<Role> roles,
                               Set<String> roleSet,
                               Set<String> permissionRoleSet) {

        if (permissionRoleSet.size() != rolePermissionImportDTOS.size()) {
            String message = messageSource.getMessage("import.permission.duplicate.permission.and.role", null, null);
            throw new SSDSBusinessException(null, message);
        }

        roleSet.removeAll(roles.stream().map(Role::getCode).collect(Collectors.toSet()));
        if (CollectionUtils.isNotEmpty(roleSet)) {
            throw new SSDSBusinessException(null, messageSource.getMessage("import.permission.roles.not.exist", Arrays.asList(StringUtils.join(roleSet, ", ")).toArray(), null));
        }

        List<String> inactivaRoles = roles.stream().filter(role -> Boolean.FALSE.equals(role.getIsActive())).map(Role::getCode).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(inactivaRoles)) {
            throw new SSDSBusinessException(null, messageSource.getMessage("import.permission.roles.inactive", Arrays.asList(StringUtils.join(inactivaRoles, ", ")).toArray(), null));
        }
    }

    private void createNewPermissions(Set<String> permissionSet, Map<String, Role> roleMap, List<RolePermissionImportDTO> rolePermissionImportDTOS) {
        Map<String, List<RolePermissionImportDTO>> importDtoByPermission = rolePermissionImportDTOS.stream().collect(Collectors.groupingBy(RolePermissionImportDTO::getPermissionCode));
        Set<Role> updateRole = new HashSet<>();
        Set<Permission> permissions = new HashSet<>();
        for (String permissionCode : permissionSet) {
            Permission permission = new Permission();
            permission.setCode(permissionCode);
            permission.setName(permissionCode);
            List<RolePermissionImportDTO> importDtos = importDtoByPermission.get(permissionCode);
            for (RolePermissionImportDTO importDto : importDtos) {
                if (Boolean.TRUE.equals(importDto.getIsActive())) {
                    Role role = roleMap.get(importDto.getRoleCode());
                    permission.getRoles().add(role);
                    role.getPermissions().add(permission);
                    updateRole.add(role);
                }
            }
            permissions.add(permission);
        }
        permissionRepository.saveAll(permissions);
        roleRepository.saveAll(updateRole);
    }

    private void updateRolePermission(List<RolePermissionImportDTO> rolePermissionImportDTOS, Map<String, Role> roleMap, List<Permission> permissions) {
        Map<String, List<RolePermissionImportDTO>> roleDTOMap = rolePermissionImportDTOS.stream().collect(Collectors.groupingBy(RolePermissionImportDTO::getRoleCode));
        List<Role> updatedRoles = new ArrayList<>();

        Map<String, Permission> permissionMap = new HashMap<>();
        for (Permission permission : permissions) {
            permissionMap.put(permission.getCode(), permission);
        }

        for (String roleCode : roleDTOMap.keySet()) {
            List<RolePermissionImportDTO> listImportDtoByRole = roleDTOMap.get(roleCode);
            List<String> updatePermissionsCode = listImportDtoByRole.stream().filter(dto -> Objects.nonNull(dto.getIsActive())).map(RolePermissionImportDTO::getPermissionCode).collect(Collectors.toList());
            List<String> activePermissionsCode = listImportDtoByRole.stream().filter(dto -> Boolean.TRUE.equals(dto.getIsActive())).map(RolePermissionImportDTO::getPermissionCode).collect(Collectors.toList());
            Role role = roleMap.get(roleCode);

            if (CollectionUtils.isNotEmpty(updatePermissionsCode)) {
                List<Permission> removedPermissions = role.getPermissions().stream().filter(permission -> updatePermissionsCode.contains(permission.getCode())).collect(Collectors.toList());
                role.getPermissions().removeAll(removedPermissions);
                for (Permission removedPermission : removedPermissions) {
                    removedPermission.getRoles().remove(role);
                }
                updatedRoles.add(role);
            }

            if (CollectionUtils.isNotEmpty(activePermissionsCode)) {
                List<Permission> activePermissions = new ArrayList<>();
                for (String permissionCode : activePermissionsCode) {
                    if (Objects.nonNull(permissionMap.get(permissionCode))) {
                        activePermissions.add(permissionMap.get(permissionCode));
                    }
                }
                role.getPermissions().addAll(activePermissions);
                for (Permission activePermission : activePermissions) {
                    activePermission.getRoles().add(role);
                }
            }
        }
        roleRepository.saveAll(updatedRoles);
    }

}
