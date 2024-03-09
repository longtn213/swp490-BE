package com.fpt.ssds.service.impl;

import com.fpt.ssds.domain.ReasonMessage;
import com.fpt.ssds.repository.ReasonMessageRepository;
import com.fpt.ssds.service.ReasonMessageService;
import com.fpt.ssds.service.dto.ReasonMessageDto;
import com.fpt.ssds.service.mapper.ReasonMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReasonMessageServiceImpl implements ReasonMessageService {
    private final ReasonMessageRepository reasonMessageRepository;

    private final ReasonMessageMapper reasonMessageMapper;

    @Override
    public List<ReasonMessageDto> getListReasonDTOByType(String reasonType) {
        return reasonMessageMapper.toDto(getListReasonByType(reasonType));
    }

    @Override
    public List<ReasonMessage> getListReasonByType(String reasonType) {
        List<ReasonMessage> lstReason = reasonMessageRepository.findActiveReasonMessageByType(reasonType);
        return lstReason;
    }
}
