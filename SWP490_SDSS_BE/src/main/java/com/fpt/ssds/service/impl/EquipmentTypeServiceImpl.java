package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.EquipmentType;
import com.fpt.ssds.domain.SpaService;
import com.fpt.ssds.repository.EquipmentTypeRepository;
import com.fpt.ssds.repository.SpaServiceRepository;
import com.fpt.ssds.service.EquipmentTypeService;
import com.fpt.ssds.service.SpaServiceService;
import com.fpt.ssds.service.dto.EquipmentTypeDto;
import com.fpt.ssds.service.mapper.EquipmentTypeMapper;
import com.fpt.ssds.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EquipmentTypeServiceImpl implements EquipmentTypeService {
    private final EquipmentTypeRepository equipmentTypeRepository;

    private final EquipmentTypeMapper equipmentTypeMapper;

    private final SpaServiceRepository spaServiceRepository;


    @Autowired
    public EquipmentTypeServiceImpl(EquipmentTypeRepository equipmentTypeRepository,
                                    EquipmentTypeMapper equipmentTypeMapper,
                                    SpaServiceRepository spaServiceRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.equipmentTypeMapper = equipmentTypeMapper;
        this.spaServiceRepository = spaServiceRepository;
    }

    @Override
    @Transactional
    public EquipmentType createUpdate(EquipmentTypeDto equipmentTypeDto) {
        if (Objects.isNull(equipmentTypeDto.getCode())) {
            equipmentTypeDto.setCode(Utils.genCodeFromName(equipmentTypeDto.getName()));
        }
        if (Objects.nonNull(equipmentTypeDto.getId())) {
            Optional<EquipmentType> equipOpt = equipmentTypeRepository.findById(equipmentTypeDto.getId());
            if (equipOpt.isEmpty()) {
                throw new SSDSBusinessException(ErrorConstants.EQUIPMENT_TYPE_NOT_EXIST);
            }
            updateEquipmentType(equipmentTypeDto);
        } else {
            createEquipmentType(equipmentTypeDto);
        }
        return equipmentTypeRepository.save(equipmentTypeMapper.toEntity(equipmentTypeDto));
    }

    @Override
    public EquipmentTypeDto getById(Long id) {
        Optional<EquipmentType> equipTypeOpt = equipmentTypeRepository.findById(id);
        if (equipTypeOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.EQUIPMENT_TYPE_NOT_EXIST, Arrays.asList(id));
        }
        return equipmentTypeMapper.toDto(equipTypeOpt.get());
    }

    @Override
    public List<EquipmentTypeDto> getALl() {
        return equipmentTypeMapper.toDto(equipmentTypeRepository.findAll());
    }

    @Override
    @Transactional
    public void deleteEquipmentType(List<Long> listId) {
        if (!CollectionUtils.isEmpty(listId)) {
            List<SpaService> services = spaServiceRepository.findByEquipmentTypeIdIn(listId);
            services.forEach(spaService -> spaService.setEquipmentType(null));
            spaServiceRepository.saveAll(services);
            equipmentTypeRepository.deleteAllById(listId);
        }
    }

    private void createEquipmentType(EquipmentTypeDto equipmentTypeDto) {
        String code = genCode(equipmentTypeDto.getName());
        equipmentTypeDto.setCode(code);
        Optional<EquipmentType> equipmentOpt = equipmentTypeRepository.findByCode(equipmentTypeDto.getCode());
        if (equipmentOpt.isPresent()) {
            EquipmentType equipmentType = equipmentOpt.get();
            throw new SSDSBusinessException(ErrorConstants.EQUIPMENT_TYPE_ALREADY_EXIST, Arrays.asList(equipmentType.getName()));
        }
    }

    private void updateEquipmentType(EquipmentTypeDto equipmentTypeDto) {
        Optional<EquipmentType> equipOpt = equipmentTypeRepository.findByCode(equipmentTypeDto.getCode());
        if (equipOpt.isPresent()) {
            if (!equipOpt.get().getId().equals(equipmentTypeDto.getId())) {
                throw new SSDSBusinessException(ErrorConstants.EQUIPMENT_TYPE_ALREADY_EXIST, Arrays.asList(equipmentTypeDto.getName()));
            }
        }
    }

    private String genCode(String equipmentTypeName) {
        return Utils.deAccent(equipmentTypeName).toUpperCase().replace(" ", "_");
    }
}
