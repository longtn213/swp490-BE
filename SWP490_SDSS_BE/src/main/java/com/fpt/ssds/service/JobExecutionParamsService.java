package com.fpt.ssds.service;

import com.fpt.ssds.domain.JobExecutionParams;
import com.fpt.ssds.service.dto.JobExecutionParamsDTO;

import java.util.List;
import java.util.Optional;

public interface JobExecutionParamsService {

    /**
     * Save a jobExecutionParams.
     *
     * @param jobExecutionParamsDTO the entity to save.
     * @return the persisted entity.
     */
    JobExecutionParamsDTO save(JobExecutionParamsDTO jobExecutionParamsDTO);

    /**
     * Get all the jobExecutionParams.
     *
     * @return the list of entities.
     */
    List<JobExecutionParamsDTO> findAll();


    /**
     * Get the "id" jobExecutionParams.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<JobExecutionParamsDTO> findOne(Long id);

    /**
     * Delete the "id" jobExecutionParams.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    JobExecutionParams findByParamKey(String paramKey);

    void save(JobExecutionParams jobExecutionParams);
}
