package com.fpt.ssds.service;

import com.fpt.ssds.domain.ReasonMapping;

public interface ReasonMappingService {
    ReasonMapping createReasonMapping(Long reasonId, Long refId, String refType, String actionType, String note);

    void createAndSaveReasonMapping(Long reasonId, Long refId, String refType, String actionType, String note);
}
