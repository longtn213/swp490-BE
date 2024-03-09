package com.fpt.ssds.service.impl;

import com.fpt.ssds.domain.Price;
import com.fpt.ssds.service.dto.PriceDto;
import com.fpt.ssds.repository.PriceRepository;
import com.fpt.ssds.service.PriceService;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.service.mapper.PriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class PriceServiceImpl implements PriceService {
    private final PriceRepository priceRepository;

    private final PriceMapper priceMapper;

    @Autowired
    public PriceServiceImpl(PriceRepository priceRepository, PriceMapper priceMapper) {
        this.priceRepository = priceRepository;
        this.priceMapper = priceMapper;
    }

    @Override
    public void createNewPriceForService(Long serviceId, Double price) {
        PriceDto priceDto = new PriceDto();
        priceDto.setPrice(price);
        priceDto.setSpaServiceId(serviceId);
        priceDto.setStartDate(Instant.now());
        priceRepository.save(priceMapper.toEntity(priceDto));
    }

    @Override
    public void updatePriceForService(SpaServiceDto spaServiceDto) {
        Optional<Price> priceOpt = priceRepository.findLastestPriceByServiceId(spaServiceDto.getId());
        if (priceOpt.isPresent()) {
            Price price = priceOpt.get();
            price.setEndDate(Instant.now());
            priceRepository.save(price);
        }
        createNewPriceForService(spaServiceDto.getId(), spaServiceDto.getCurrentPrice());
    }
}
