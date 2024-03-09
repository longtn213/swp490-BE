package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.ScaForm;
import com.fpt.ssds.service.dto.SCAFormDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface ScaFormMapper extends EntityMapper<SCAFormDto, ScaForm> {
    @Override
    SCAFormDto toDto(ScaForm entity);

    @Mapping(target = "questions", ignore = true)
    ScaForm toEntity(SCAFormDto scaFormDto);

    default ScaForm fromId(Long id) {
        if (id == null) {
            return null;
        }
        ScaForm scaForm = new ScaForm();
        scaForm.setId(id);
        return scaForm;
    }
}
