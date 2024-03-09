package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.SpaServiceDto;

public interface PriceService {
    void createNewPriceForService(Long serviceId, Double price);

    void updatePriceForService(SpaServiceDto spaServiceDto);
}
