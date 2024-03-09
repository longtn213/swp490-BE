package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.Category;
import com.fpt.ssds.domain.SpaService;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.repository.CategoryRepository;
import com.fpt.ssds.repository.SpaServiceRepository;
import com.fpt.ssds.service.EquipmentTypeService;
import com.fpt.ssds.service.FileService;
import com.fpt.ssds.service.PriceService;
import com.fpt.ssds.service.SpaServiceService;
import com.fpt.ssds.service.dto.FileDto;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.service.mapper.SpaServiceMapper;
import com.fpt.ssds.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SpaServiceServiceImpl implements SpaServiceService {

    private final SpaServiceRepository spaServiceRepository;

    private final CategoryRepository categoryRepository;

    private final FileService fileService;

    private final EquipmentTypeService equipmentTypeService;

    private final SpaServiceMapper spaServiceMapper;


    private final PriceService priceService;


    @Override
    @Transactional
    public SpaService createUpdate(SpaServiceDto spaServiceDto) {
        if (Objects.isNull(spaServiceDto.getCode())) {
            spaServiceDto.setCode(Utils.genCodeFromName(spaServiceDto.getName()));
        }

        if (Objects.nonNull(spaServiceDto.getId())) {
            Optional<SpaService> serviceOpt = spaServiceRepository.findById(spaServiceDto.getId());
            if (serviceOpt.isEmpty()) {
                throw new SSDSBusinessException(ErrorConstants.SPA_SERVICE_NOT_EXIST, Arrays.asList(spaServiceDto.getId()));
            }
            return updateService(spaServiceDto);
        } else {
            return createService(spaServiceDto);
        }
    }

    @Override
    public SpaServiceDto getById(Long serviceId) {
        Optional<SpaService> serviceOpt = spaServiceRepository.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.SPA_SERVICE_NOT_EXIST, Arrays.asList(serviceId));
        }
        List<FileDto> files = fileService.findByTypeAndRefIdAndUploadStatus(FileType.SERVICE, serviceId, UploadStatus.SUCCESS);
        SpaServiceDto spaServiceDto = spaServiceMapper.toDto(serviceOpt.get());
        spaServiceDto.setFiles(files);
        return spaServiceDto;
    }

    @Override
    public List<SpaServiceDto> getServicesNotAssignedCategory() {
        return spaServiceMapper.toDto(spaServiceRepository.findByCategoryIdIsNull());
    }

    private SpaService createService(SpaServiceDto spaServiceDto) {
        Optional<SpaService> serviceOpt = spaServiceRepository.findByCode(spaServiceDto.getCode());
        if (serviceOpt.isPresent()) {
            throw new SSDSBusinessException(ErrorConstants.SPA_SERVICE_CODE_ALREADY_EXIST, Arrays.asList(spaServiceDto.getCode()));
        }
        validateCommon(spaServiceDto);

        SpaService spaService = spaServiceRepository.save(spaServiceMapper.toEntity(spaServiceDto));
        priceService.createNewPriceForService(spaService.getId(), spaService.getCurrentPrice());

        List<FileDto> files = spaServiceDto.getFiles();
        if (CollectionUtils.isNotEmpty(files)) {
            fileService.updateFileRefId(files, spaService.getId());
        }
        return spaService;
    }

    private SpaService updateService(SpaServiceDto spaServiceDto) {
        Optional<SpaService> serviceOpt = spaServiceRepository.findByCode(spaServiceDto.getCode());
        if (serviceOpt.isPresent()) {
            if (!spaServiceDto.getId().equals(serviceOpt.get().getId())) {
                throw new SSDSBusinessException(ErrorConstants.SPA_SERVICE_CODE_ALREADY_EXIST, Arrays.asList(spaServiceDto.getCode()));
            }
        }
        validateCommon(spaServiceDto);
        if (!spaServiceDto.getCurrentPrice().equals(serviceOpt.get().getCurrentPrice())) {
            priceService.updatePriceForService(spaServiceDto);
        }
        SpaService spaService = spaServiceMapper.toEntity(spaServiceDto);
        spaService = spaServiceRepository.save(spaService);

        List<FileDto> files = spaServiceDto.getFiles();
        if (CollectionUtils.isNotEmpty(files)) {
            fileService.updateFileRefId(files, spaService.getId());
        }
        return spaService;
    }

    private void validateCommon(SpaServiceDto spaServiceDto) {
        Long categoryId = spaServiceDto.getCategoryId();
        if (Objects.nonNull(categoryId)) {
            Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
            if (categoryOptional.isEmpty()) {
                throw new SSDSBusinessException(ErrorConstants.CATEGORY_NOT_EXIST, Arrays.asList(categoryId));
            }
        }

        Long equipmentTypeId = spaServiceDto.getEquipmentTypeId();
        if (Objects.nonNull(equipmentTypeId)) {
            equipmentTypeService.getById(equipmentTypeId);
        }
    }
}
