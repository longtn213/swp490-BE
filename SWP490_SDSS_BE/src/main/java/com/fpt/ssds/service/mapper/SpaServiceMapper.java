package com.fpt.ssds.service.mapper;


import com.fpt.ssds.domain.SpaService;
import com.fpt.ssds.service.dto.SpaServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link SpaService} and its DTO {@link SpaServiceDto}.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class, EquipmentTypeMapper.class})
public interface SpaServiceMapper extends EntityMapper<SpaServiceDto, SpaService> {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "equipmentType.id", target = "equipmentTypeId")
    @Mapping(source = "equipmentType.name", target = "equipmentName")
    @Mapping(source = "category.code", target = "categoryCode")
    SpaServiceDto toDto(SpaService location);

    @Mapping(source = "categoryId", target = "category")
    @Mapping(source = "equipmentTypeId", target = "equipmentType")
    SpaService toEntity(SpaServiceDto locationDTO);

    default SpaService fromId(Long id) {
        if (id == null) {
            return null;
        }
        SpaService spaService = new SpaService();
        spaService.setId(id);
        return spaService;
    }
}
