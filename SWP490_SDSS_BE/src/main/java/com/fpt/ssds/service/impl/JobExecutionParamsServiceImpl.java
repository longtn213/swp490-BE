package com.fpt.ssds.service.impl;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.JobExecutionParams;
import com.fpt.ssds.repository.JobExecutionParamsRepository;
import com.fpt.ssds.service.JobExecutionParamsService;
import com.fpt.ssds.service.dto.JobExecutionParamsDTO;
import com.fpt.ssds.service.mapper.JobExecutionParamsMapper;
import com.fpt.ssds.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class JobExecutionParamsServiceImpl implements JobExecutionParamsService {
    private final JobExecutionParamsRepository jobExecutionParamsRepository;

    private final JobExecutionParamsMapper jobExecutionParamsMapper;

    @Value("${ssds.config.timezone}")
    String systemTimezone;

    @Override
    public JobExecutionParamsDTO save(JobExecutionParamsDTO jobExecutionParamsDTO) {
        log.debug("Request to save JobExecutionParams : {}", jobExecutionParamsDTO);
        JobExecutionParams jobExecutionParams = jobExecutionParamsMapper.toEntity(jobExecutionParamsDTO);
        jobExecutionParams = jobExecutionParamsRepository.save(jobExecutionParams);
        return jobExecutionParamsMapper.toDto(jobExecutionParams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobExecutionParamsDTO> findAll() {
        log.debug("Request to get all JobExecutionParams");
        return jobExecutionParamsRepository.findAll().stream()
            .map(jobExecutionParamsMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<JobExecutionParamsDTO> findOne(Long id) {
        log.debug("Request to get JobExecutionParams : {}", id);
        return jobExecutionParamsRepository.findById(id)
            .map(jobExecutionParamsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete JobExecutionParams : {}", id);
        jobExecutionParamsRepository.deleteById(id);
    }

    @Override
    public JobExecutionParams findByParamKey(String paramKey) {
        Instant now = Instant.now();
        JobExecutionParams jobExecutionParams = jobExecutionParamsRepository.findByParamKey(paramKey);

        if (Objects.isNull(jobExecutionParams)) {
            jobExecutionParams = new JobExecutionParams();
            jobExecutionParams.setParamKey(paramKey);
            jobExecutionParams.setParamValue(String.valueOf(DateUtils.atStartOfDay(now, systemTimezone).toEpochMilli()));
            jobExecutionParamsRepository.save(jobExecutionParams);
        }

        return jobExecutionParams;
    }

    @Override
    public void save(JobExecutionParams jobExecutionParams) {
        jobExecutionParamsRepository.save(jobExecutionParams);
    }
}
