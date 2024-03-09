package com.fpt.ssds.service;

import com.fpt.ssds.domain.EquipmentType;
import com.fpt.ssds.service.dto.EquipmentTypeDto;

import java.util.List;

public interface EquipmentTypeService {
    EquipmentType createUpdate(EquipmentTypeDto equipmentTypeDto);

    EquipmentTypeDto getById(Long id);

    List<EquipmentTypeDto> getALl();

    void deleteEquipmentType(List<Long> lstId);
}
