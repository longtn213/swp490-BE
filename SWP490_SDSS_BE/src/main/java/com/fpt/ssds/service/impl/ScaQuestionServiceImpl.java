package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.Option;
import com.fpt.ssds.domain.ScaQuestion;
import com.fpt.ssds.repository.SCAQuestionRepository;
import com.fpt.ssds.service.ScaOptionService;
import com.fpt.ssds.service.ScaQuestionService;
import com.fpt.ssds.service.dto.OptionDto;
import com.fpt.ssds.service.dto.SCAQuestionDto;
import com.fpt.ssds.service.mapper.SCAQuestionMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScaQuestionServiceImpl implements ScaQuestionService {
    private final SCAQuestionRepository scaQuestionRepository;

    private final SCAQuestionMapper scaQuestionMapper;

    private final ScaOptionService scaOptionService;

    @Autowired
    public ScaQuestionServiceImpl(SCAQuestionRepository scaQuestionRepository, SCAQuestionMapper scaQuestionMapper, ScaOptionService scaOptionService) {
        this.scaQuestionRepository = scaQuestionRepository;
        this.scaQuestionMapper = scaQuestionMapper;
        this.scaOptionService = scaOptionService;
    }

    @Override
    public void createUpdateQuestion(Set<SCAQuestionDto> scaQuestionDtoList) {
        for (SCAQuestionDto scaQuestionDto : scaQuestionDtoList) {
            if (!Objects.nonNull(scaQuestionDto.getId())) {
                createScaQuestion(scaQuestionDto);
            } else {
                updateScaQuestion(scaQuestionDto);
            }
        }
    }

    private void createScaQuestion(SCAQuestionDto scaQuestionDto) {
        ScaQuestion scaQuestion = scaQuestionRepository.save(scaQuestionMapper.toEntity(scaQuestionDto));
        Set<OptionDto> optionDtoList = scaQuestionDto.getOptions();
        optionDtoList.forEach(optionDto -> optionDto.setQuestionId(scaQuestion.getId()));
        scaOptionService.createUpdateOption(optionDtoList);
    }


    private void updateScaQuestion(SCAQuestionDto scaQuestionDto) {
        Optional<ScaQuestion> scaQuestionOptional = scaQuestionRepository.findById(scaQuestionDto.getId());
        if (scaQuestionOptional.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.SCA_QUESTION_NOT_EXIST, Collections.singletonList(scaQuestionDto.getId()));
        }
        scaQuestionDto.setFormId(scaQuestionOptional.get().getForm().getId());
        ScaQuestion scaQuestionUpdate = scaQuestionMapper.toEntity(scaQuestionDto);
        scaQuestionRepository.save(scaQuestionUpdate);
        Set<OptionDto> optionDtoList = scaQuestionDto.getOptions();
        optionDtoList.forEach(optionDto -> optionDto.setQuestionId(scaQuestionUpdate.getId()));
        scaOptionService.createUpdateOption(optionDtoList);
    }


    @Override
    @Transactional
    public void deleteScaQuestion(Long id) {
        Optional<ScaQuestion> scaQuestionOptional = scaQuestionRepository.findById(id);
        if (scaQuestionOptional.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.SCA_QUESTION_NOT_EXIST, Collections.singletonList(id));
        }
        ScaQuestion scaQuestion = scaQuestionOptional.get();
        List<Option> options = scaQuestion.getOptions();
        if (CollectionUtils.isNotEmpty(options)) {
            scaOptionService.deleteListScaOption(options.stream().map(Option::getId).collect(Collectors.toList()));
        }
        scaQuestionRepository.deleteById(id);

    }
}
