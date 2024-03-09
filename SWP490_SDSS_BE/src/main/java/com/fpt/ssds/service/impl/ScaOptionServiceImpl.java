package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.Option;
import com.fpt.ssds.repository.ScaOptionRepository;
import com.fpt.ssds.service.ScaOptionService;
import com.fpt.ssds.service.dto.OptionDto;
import com.fpt.ssds.service.mapper.OptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScaOptionServiceImpl implements ScaOptionService {

    private final ScaOptionRepository scaOptionRepository;

    private final OptionMapper optionSCAQuestionMapper;

    @Autowired
    public ScaOptionServiceImpl(
        ScaOptionRepository scaOptionRepository,
        OptionMapper optionSCAQuestionMapper
    ) {
        this.scaOptionRepository = scaOptionRepository;
        this.optionSCAQuestionMapper = optionSCAQuestionMapper;
    }

    @Override
    public void createUpdateOption(Set<OptionDto> optionDtoList) {
        for (OptionDto optionDto : optionDtoList) {
            if (!Objects.nonNull(optionDto.getId())) {
                createOption(optionDto);
            } else {
                updateOption(optionDto);
            }
        }

    }

    public void updateOption(OptionDto optionDto) {
        Optional<Option> optionOpt = scaOptionRepository.findById(optionDto.getId());
        if (optionOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.OPTION_NOT_EXIST, Collections.singletonList(optionDto.getId()));
        }
        optionDto.setQuestionId(optionOpt.get().getQuestion().getId());
        Option updateOption = optionSCAQuestionMapper.toEntity(optionDto);
        scaOptionRepository.save(updateOption);

    }

    public void createOption(OptionDto optionDto) {
        scaOptionRepository.save(optionSCAQuestionMapper.toEntity(optionDto));
    }


    @Override
    public void deleteListScaOption(List<Long> listId) {
        List<Option> existedOption = scaOptionRepository.findAllById(listId);
        scaOptionRepository.deleteAllById(existedOption.stream().map(Option::getId).collect(Collectors.toList()));
    }

}
