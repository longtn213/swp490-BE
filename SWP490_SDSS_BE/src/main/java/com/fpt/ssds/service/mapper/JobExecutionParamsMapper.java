package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.JobExecutionParams;
import com.fpt.ssds.service.dto.JobExecutionParamsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface JobExecutionParamsMapper extends EntityMapper<JobExecutionParamsDTO, JobExecutionParams> {

    default JobExecutionParams fromId(Long id) {
        if (id == null) {
            return null;
        }
        JobExecutionParams jobExecutionParams = new JobExecutionParams();
        jobExecutionParams.setId(id);
        return jobExecutionParams;
    }
}
