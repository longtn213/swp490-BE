package com.fpt.ssds.service;

import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.domain.ConfigData;
import com.fpt.ssds.service.dto.ConfigDataDTO;
import com.fpt.ssds.service.dto.WarningResponseDTO;

public interface ConfigDataService {
    Integer getIntegerByKey(Long branchId, String key, Integer defaultValue);

    Boolean getBooleanByKey(Long branchId, String key, Boolean defaultValue);

    String getStringByKey(Long branchId, String key, String defaultValue);

    ConfigDataDTO getById(Long id);

    Integer getIntegerByKey(String key, Integer defaultValue);

    void createConfigForNewBranch(Branch branch);

    WarningResponseDTO updateConfig(ConfigDataDTO configDataDTO);
}
