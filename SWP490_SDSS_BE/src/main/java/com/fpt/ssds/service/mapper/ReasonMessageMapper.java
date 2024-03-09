package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.ReasonMessage;
import com.fpt.ssds.service.dto.ReasonMessageDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ReasonMessageTypeMapper.class})
public interface ReasonMessageMapper extends EntityMapper<ReasonMessageDto, ReasonMessage> {

    default ReasonMessage fromId(Long id) {
        if (id == null) {
            return null;
        }
        ReasonMessage reasonMessage = new ReasonMessage();
        reasonMessage.setId(id);
        return reasonMessage;
    }
}
