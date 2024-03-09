package com.fpt.ssds.service.impl;

import com.fpt.ssds.domain.Permission;
import com.fpt.ssds.domain.Role;
import com.fpt.ssds.repository.PermissionRepository;
import com.fpt.ssds.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

//    @Override
//    @Transactional
//    public Map<String, List<String>> getMapPermission() {
//        Map<String, List<String>> mapPermission = new HashMap<>();
//        List<Permission> permissions = permissionRepository.findAll();
//        for (Permission permission : permissions) {
//            if (StringUtils.isNotEmpty(permission.getPath())) {
//                List<String> lstRoleCode = new ArrayList<>();
//                for (Role role : permission.getRoles()) {
//                    lstRoleCode.add(role.getCode());
//                }
//                mapPermission.put(permission.getPath(), lstRoleCode);
//            }
//        }
//        return mapPermission;
//    }
}
