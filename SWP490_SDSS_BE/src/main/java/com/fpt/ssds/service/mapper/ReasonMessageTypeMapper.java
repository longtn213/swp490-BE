package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.ReasonMessageType;
import com.fpt.ssds.service.dto.ReasonMessageTypeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface ReasonMessageTypeMapper extends EntityMapper<ReasonMessageTypeDto, ReasonMessageType> {
    default ReasonMessageType fromId(Long id) {
        if (id == null) {
            return null;
        }
        ReasonMessageType reasonMessageType = new ReasonMessageType();
        reasonMessageType.setId(id);
        return reasonMessageType;
    }
}
