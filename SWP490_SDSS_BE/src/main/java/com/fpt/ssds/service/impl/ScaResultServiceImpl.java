package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.Lookup;
import com.fpt.ssds.domain.ScaResult;
import com.fpt.ssds.domain.SpaService;
import com.fpt.ssds.domain.User;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.repository.ScaResultRepository;
import com.fpt.ssds.repository.SpaServiceRepository;
import com.fpt.ssds.service.*;
import com.fpt.ssds.service.dto.FileDto;
import com.fpt.ssds.service.dto.QuestionAnswerDto;
import com.fpt.ssds.service.dto.ScaResultDto;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.service.mapper.ScaResultMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScaResultServiceImpl implements ScaResultService {
    private final ScaResultRepository scaResultRepository;

    private final ScaResultMapper scaResultMapper;

    private final QuestionAnswerService questionAnswerService;
    private final UserService userService;

    private final LookupService lookupService;

    private final SpaServiceRepository spaServiceRepository;

    private final FileService fileService;

    @Override
    @Transactional
    public ScaResultDto findById(Long id) {
        Optional<ScaResult> scaResult = scaResultRepository.findById(id);
        if (scaResult.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.SCA_RESULT_NOT_EXIST, List.of(id));
        }
        List<FileDto> files = fileService.findByTypeAndRefIdAndUploadStatus(FileType.SCA_RESULT, id, UploadStatus.SUCCESS);
        ScaResultDto scaResultDto = scaResultMapper.toDto(scaResult.get());
        scaResultDto.setFiles(files);
        return scaResultDto;
    }


    @Override
    @Transactional
    public List<ScaResultDto> getAll() {
        List<ScaResult> scaResultDtoList = scaResultRepository.findAll();
        return getScaResultDtos(scaResultDtoList);
    }

    @Override
    @Transactional
    public List<ScaResultDto> findByCustomerId(Long id) {
        List<ScaResult> scaResultDtoList = scaResultRepository.findByCustomerId(id);
        return getScaResultDtos(scaResultDtoList);
    }

    @NotNull
    private List<ScaResultDto> getScaResultDtos(List<ScaResult> scaResultDtoList) {
        List<ScaResultDto> scaResultReturn = new ArrayList<>();
        for (ScaResult scaResult : scaResultDtoList) {
            List<FileDto> files = fileService.findByTypeAndRefIdAndUploadStatus(FileType.SCA_RESULT, scaResult.getId(), UploadStatus.SUCCESS);
            ScaResultDto scaResultDto = scaResultMapper.toDto(scaResult);
            scaResultDto.setFiles(files);
            scaResultReturn.add(scaResultDto);
        }

        return scaResultReturn;
    }

    @Override
    @Transactional
    public void createUpdate(ScaResultDto scaResultDto) {
        if (Objects.isNull(scaResultDto.getId())) {
            createScaResult(scaResultDto);
        } else {
            updateScaResult(scaResultDto);
        }
    }

    private void updateScaResult(ScaResultDto scaResultDto) {
        Optional<ScaResult> resultDto = scaResultRepository.findById(scaResultDto.getId());
        if (resultDto.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.SCA_RESULT_NOT_EXIST, List.of(scaResultDto.getId()));
        }
        Lookup status = lookupService.findByKeyAndCode(Constants.LOOKUP_KEY.FORM_RESULT_STATUS, Constants.FORM_RESULT_STATUS.DONE_ANSWER);
        Optional<User> repliedBy = userService.findById(scaResultDto.getRepliedBy().getId());
        if (repliedBy.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.USER_NOT_EXIST, List.of(scaResultDto.getId()));
        }
        ScaResult updateScaResult = resultDto.get();
        updateScaResult.setStatus(status);
        updateScaResult.setComment(scaResultDto.getComment());
        updateScaResult.setRepliedBy(repliedBy.get());

        List<Long> servicesId = scaResultDto.getSpaServices().stream().filter(service -> Objects.nonNull(service.getId())).map(SpaServiceDto::getId).collect(Collectors.toList());
        List<SpaService> recommendedServices = spaServiceRepository.findAllById(servicesId);
        updateScaResult.setSpaServices(recommendedServices);
        scaResultRepository.save(updateScaResult);
    }

    private void createScaResult(ScaResultDto scaResultDto) {
        User customer = userService.findByRoleAndId(scaResultDto.getCustomer().getId(), Constants.ROLE.CUSTOMER);

        Lookup status = lookupService.findByKeyAndCode(Constants.LOOKUP_KEY.FORM_RESULT_STATUS, Constants.FORM_RESULT_STATUS.WAITING_FOR_RESULT);
        ScaResult scaResult = new ScaResult();
        scaResult.setCustomer(customer);
        scaResult.setStatus(status);
        scaResultRepository.save(scaResult);
        List<QuestionAnswerDto> questionAnswerDtos = scaResultDto.getAnswerSet();
        questionAnswerDtos.forEach(questionAnswerDto -> questionAnswerDto.setResultId(scaResult.getId()));
        questionAnswerService.createQuestionAnswer(questionAnswerDtos);
        List<FileDto> files = scaResultDto.getFiles();
        if (CollectionUtils.isNotEmpty(files)) {
            fileService.updateFileRefId(files, scaResult.getId());
        }
    }

}
