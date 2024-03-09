package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.SCAQuestionDto;

import java.util.Set;

public interface ScaQuestionService {

    void createUpdateQuestion(Set<SCAQuestionDto> scaQuestionDto);

    void deleteScaQuestion(Long id);
}
