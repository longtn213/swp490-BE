package com.fpt.ssds.service.impl;

import com.fpt.ssds.domain.ReasonMapping;
import com.fpt.ssds.domain.ReasonMessage;
import com.fpt.ssds.repository.ReasonMappingRepository;
import com.fpt.ssds.service.ReasonMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReasonMappingServiceImpl implements ReasonMappingService {
    private final ReasonMappingRepository reasonMappingRepository;

    @Override
    public ReasonMapping createReasonMapping(Long reasonId, Long refId, String refType, String actionType, String note) {
        ReasonMessage reason = new ReasonMessage();
        reason.setId(reasonId);

        ReasonMapping reasonMapping = new ReasonMapping();
        reasonMapping.setReasonMessage(reason);
        reasonMapping.setRefId(refId);
        reasonMapping.setRefType(refType);
        reasonMapping.setActionType(actionType);
        reasonMapping.setReasonNote(note);
        return reasonMapping;
    }

    @Override
    public void createAndSaveReasonMapping(Long reasonId, Long refId, String refType, String actionType, String note) {
        ReasonMapping reasonMapping = createReasonMapping(reasonId, refId, refType, actionType, note);
        reasonMappingRepository.save(reasonMapping);
    }
}
