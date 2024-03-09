package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.EquipmentTypeService;
import com.fpt.ssds.service.dto.CategoryDto;
import com.fpt.ssds.service.dto.EquipmentTypeDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/equipment-type")
public class EquipmentTypeController {
    private final EquipmentTypeService equipmentTypeService;

    @Autowired
    public EquipmentTypeController(EquipmentTypeService equipmentTypeService) {
        this.equipmentTypeService = equipmentTypeService;
    }

    @PostMapping("")
    public ResponseEntity<ResponseDTO> createUpdate(@RequestBody @Valid EquipmentTypeDto equipmentTypeDto) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(equipmentTypeService.createUpdate(equipmentTypeDto)));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAll() {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(equipmentTypeService.getALl()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(equipmentTypeService.getById(id)));
    }

    @PostMapping("delete")
    public ResponseEntity<ResponseDTO> deleteById(@RequestBody List<Long> listId) {
        equipmentTypeService.deleteEquipmentType(listId);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }
}
