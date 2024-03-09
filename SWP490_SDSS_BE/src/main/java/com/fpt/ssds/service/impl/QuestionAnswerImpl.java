package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.QuestionAnswer;
import com.fpt.ssds.repository.QuestionAnswerRepository;
import com.fpt.ssds.service.QuestionAnswerService;
import com.fpt.ssds.service.dto.QuestionAnswerDto;
import com.fpt.ssds.service.mapper.QuestionAnswerMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionAnswerImpl implements QuestionAnswerService {

    private final QuestionAnswerRepository questionAnswerRepository;

    private final QuestionAnswerMapper questionAnswerMapper;

    public QuestionAnswerImpl(QuestionAnswerRepository questionAnswerRepository, QuestionAnswerMapper questionAnswerMapper) {
        this.questionAnswerRepository = questionAnswerRepository;
        this.questionAnswerMapper = questionAnswerMapper;
    }


    @Override
    @Transactional
    public QuestionAnswerDto findById(Long id) {
        Optional<QuestionAnswer> questionAnswer = questionAnswerRepository.findById(id);
        if (questionAnswer.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.QUESTION_ANSWER_NOT_EXIST, List.of(id));
        }

        return questionAnswerMapper.toDto(questionAnswer.get());
    }

    @Override
    public void createQuestionAnswer(List<QuestionAnswerDto> questionAnswerDtos) {
        for (QuestionAnswerDto questionAnswerDto : questionAnswerDtos) {
            questionAnswerRepository.save(questionAnswerMapper.toEntity(questionAnswerDto));
        }
    }
}
