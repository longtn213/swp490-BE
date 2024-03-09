package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.SCAFormDto;

import java.util.List;

public interface ScaFormService {
    void createUpdate(SCAFormDto scaFormDto);

    List<SCAFormDto> getAll();

    SCAFormDto findById(Long id);

}
