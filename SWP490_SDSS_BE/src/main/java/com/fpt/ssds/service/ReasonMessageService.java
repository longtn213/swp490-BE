package com.fpt.ssds.service;

import com.fpt.ssds.domain.ReasonMessage;
import com.fpt.ssds.service.dto.ReasonMessageDto;

import java.util.List;

public interface ReasonMessageService {
    List<ReasonMessageDto> getListReasonDTOByType(String type);

    List<ReasonMessage> getListReasonByType(String reasonType);
}
