package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.ScaResultDto;

import java.util.List;

public interface ScaResultService {

    ScaResultDto findById(Long id);

    void createUpdate(ScaResultDto scaResultDto);

    List<ScaResultDto> getAll();

    List<ScaResultDto> findByCustomerId(Long id);

}
