package com.fpt.ssds.service;

import com.fpt.ssds.domain.SpaService;
import com.fpt.ssds.service.dto.SpaServiceDto;

import java.util.List;

public interface SpaServiceService {
    SpaService createUpdate(SpaServiceDto spaServiceDto);

    SpaServiceDto getById(Long id);

    List<SpaServiceDto> getServicesNotAssignedCategory();
}
