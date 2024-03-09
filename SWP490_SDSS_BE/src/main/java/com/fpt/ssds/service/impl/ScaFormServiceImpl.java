package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.ScaForm;
import com.fpt.ssds.repository.SCAFormRepository;
import com.fpt.ssds.service.ScaFormService;
import com.fpt.ssds.service.ScaQuestionService;
import com.fpt.ssds.service.dto.SCAFormDto;
import com.fpt.ssds.service.dto.SCAQuestionDto;
import com.fpt.ssds.service.mapper.ScaFormMapper;
import com.fpt.ssds.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@Service
public class ScaFormServiceImpl implements ScaFormService {

    private final SCAFormRepository scaFormRepository;

    private final ScaFormMapper scaFormMapper;

    private final ScaQuestionService scaQuestionService;

    @Autowired
    public ScaFormServiceImpl(SCAFormRepository scaFormRepository, ScaFormMapper scaFormMapper, ScaQuestionService scaQuestionService) {
        this.scaFormRepository = scaFormRepository;
        this.scaFormMapper = scaFormMapper;
        this.scaQuestionService = scaQuestionService;
    }

    @Override
    public void createUpdate(SCAFormDto scaFormDto) {
        if (!Objects.nonNull(scaFormDto.getId())) {
            if (Objects.isNull(scaFormDto.getCode())) {
                scaFormDto.setCode(Utils.genCodeFromName(scaFormDto.getName()));
            }
            createScaForm(scaFormDto);
        } else {
            updateScaForm(scaFormDto);
        }
    }

    private void createScaForm(@Valid SCAFormDto scaFormDto) {
        Optional<ScaForm> scaFormOptional = scaFormRepository.findByCode(scaFormDto.getCode());
        if (scaFormOptional.isPresent()) {
            throw new SSDSBusinessException(ErrorConstants.SCA_FORM_DUPLICATE_CODE, Collections.singletonList(scaFormDto.getCode()));
        }
        scaFormDto.setActive(true);
        ScaForm scaForm = scaFormRepository.save(scaFormMapper.toEntity(scaFormDto));
        Set<SCAQuestionDto> scaQuestionDtoList = scaFormDto.getQuestions();
        scaQuestionDtoList.forEach(scaQuestionDto -> scaQuestionDto.setFormId(scaForm.getId()));
        scaQuestionService.createUpdateQuestion(scaQuestionDtoList);
    }

    private void updateScaForm(SCAFormDto scaFormDto) {
        Optional<ScaForm> scaFormOptional = scaFormRepository.findById(scaFormDto.getId());
        if (scaFormOptional.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.SCA_FORM_NOT_EXIST, Collections.singletonList(scaFormDto.getId()));
        }
        ScaForm scaFormUpdate = scaFormMapper.toEntity(scaFormDto);
        scaFormRepository.save(scaFormUpdate);
        Set<SCAQuestionDto> scaQuestionDtoList = scaFormDto.getQuestions();
        scaQuestionDtoList.forEach(scaQuestionDto -> scaQuestionDto.setFormId(scaFormUpdate.getId()));
        scaQuestionService.createUpdateQuestion(scaQuestionDtoList);
    }

    @Override
    @Transactional
    public List<SCAFormDto> getAll() {
        List<ScaForm> scaFormDtoList = scaFormRepository.findAll();
        return scaFormMapper.toDto(scaFormDtoList);
    }

    @Override
    @Transactional
    public SCAFormDto findById(Long id) {
        Optional<ScaForm> scaFormOptional = scaFormRepository.findById(id);
        if (scaFormOptional.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.SCA_FORM_NOT_EXIST, List.of(id));
        }
        return scaFormMapper.toDto(scaFormOptional.get());
    }

}
