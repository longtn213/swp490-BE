package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.ErrorMessage;
import com.fpt.ssds.service.dto.ErrorMessageDto;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link ErrorMessage} and its DTO {@link ErrorMessageDto}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ErrorMessageMapper extends EntityMapper<ErrorMessageDto, ErrorMessage> {

    default ErrorMessage fromId(Long id) {
        if (id == null) {
            return null;
        }
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setId(id);
        return errorMessage;
    }
}

