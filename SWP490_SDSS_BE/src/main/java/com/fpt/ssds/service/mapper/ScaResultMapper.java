package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.ScaResult;
import com.fpt.ssds.service.dto.ScaResultDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {LookupMapper.class, SpaServiceMapper.class, UserMapper.class})
public interface ScaResultMapper extends EntityMapper<ScaResultDto, ScaResult> {
    @Override
    ScaResult toEntity(ScaResultDto dto);

    @Override
    ScaResultDto toDto(ScaResult entity);

    default ScaResult fromId(Long id) {
        if (id == null) {
            return null;
        }
        ScaResult scaResult = new ScaResult();
        scaResult.setId(id);
        return scaResult;
    }
}
