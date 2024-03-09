package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.EquipmentType;
import com.fpt.ssds.service.dto.EquipmentTypeDto;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link EquipmentType} and its DTO {@link EquipmentTypeDto}.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface EquipmentTypeMapper extends EntityMapper<EquipmentTypeDto, EquipmentType>{
    default EquipmentType fromId(Long id) {
        if (id == null) {
            return null;
        }
        EquipmentType equipmentType = new EquipmentType();
        equipmentType.setId(id);
        return equipmentType;
    }
}
