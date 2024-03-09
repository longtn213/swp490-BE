package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.RoleService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.RolePermissionImportDTO;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("${ssds.api.ref.internal}/web/v1/role")
@RequiredArgsConstructor
@RestController
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/role-permission/import")
    public ResponseEntity<ResponseDTO> importRolePermission(@RequestBody @Valid List<RolePermissionImportDTO> rolePermissionImportDTOS) {
        roleService.importRolePermission(rolePermissionImportDTOS);
        return ResponseEntity.ok(ResponseUtils.responseOK(null));
    }
}
