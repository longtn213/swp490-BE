package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.QuestionAnswerDto;

import java.util.List;

public interface QuestionAnswerService {

    QuestionAnswerDto findById(Long id);

    void createQuestionAnswer(List<QuestionAnswerDto> questionAnswerDto);
}
