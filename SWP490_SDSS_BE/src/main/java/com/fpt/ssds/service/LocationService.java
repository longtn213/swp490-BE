package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.GenerateLocationDto;
import com.fpt.ssds.service.dto.LocationDTO;

import java.util.List;

public interface LocationService {
    String generateLocation(List<GenerateLocationDto> generateLocationDto);

    List<LocationDTO> getChildDivisionList(Long divisionId);
}
