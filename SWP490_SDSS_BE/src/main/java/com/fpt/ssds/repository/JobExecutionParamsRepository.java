package com.fpt.ssds.repository;

import com.fpt.ssds.domain.JobExecutionParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobExecutionParamsRepository extends JpaRepository<JobExecutionParams, Long> {
    JobExecutionParams findByParamKey(String paramKey);
}
