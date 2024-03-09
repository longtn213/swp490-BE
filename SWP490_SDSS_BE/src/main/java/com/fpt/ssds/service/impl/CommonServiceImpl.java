package com.fpt.ssds.service.impl;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.repository.*;
import com.fpt.ssds.service.CommonService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.SelectionDTO;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {
    private final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final LookupRepository lookupRepository;

    private final ReasonMessageRepository reasonMessageRepository;

    private final BranchRepository branchRepository;


    @Override
    public ResponseDTO getSelectionByType(String type) {
        ResponseDTO responseDTO = ResponseUtils.responseOK(null);
        List<SelectionDTO> selectionDTOS = new ArrayList<>();
        switch (type) {
            case Constants.COMMON_TYPE.SERVICE_CATEGORY:
                categoryRepository.findAll().forEach(category -> {
                    selectionDTOS.add(populateSelectionDTO(category.getId(), category.getCode(), category.getName()));
                });
                break;

            case Constants.COMMON_TYPE.EQUIPMENT_TYPE:
                equipmentTypeRepository.findAll().forEach(equipType -> {
                    selectionDTOS.add(populateSelectionDTO(equipType.getId(), equipType.getCode(), equipType.getName()));
                });
                break;

            case Constants.COMMON_TYPE.APPOINTMENT_MASTER_STATUS:
                lookupRepository.findByLookupKey(Constants.LOOKUP_KEY.APPOINTMENT_MASTER_STATUS).forEach(lookup -> {
                    selectionDTOS.add(populateSelectionDTO(lookup.getId(), lookup.getCode(), lookup.getName()));
                });
                break;

            case Constants.COMMON_TYPE.APPOINTMENT_SERVICE_STATUS:
                lookupRepository.findByLookupKey(Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS).forEach(lookup -> {
                    selectionDTOS.add(populateSelectionDTO(lookup.getId(), lookup.getCode(), lookup.getName()));
                });
                break;

            case Constants.COMMON_TYPE.OVERDUE_STATUS:
                lookupRepository.findByLookupKey(Constants.LOOKUP_KEY.OVERDUE_STATUS).forEach(lookup -> {
                    selectionDTOS.add(populateSelectionDTO(lookup.getId(), lookup.getCode(), lookup.getName()));
                });
                break;
            case Constants.COMMON_TYPE.REASON_MESSAGE:
                reasonMessageRepository.findAll().forEach(reasonMessage -> {
                    selectionDTOS.add(populateSelectionDTO(reasonMessage.getId(), reasonMessage.getCode(), reasonMessage.getTitle()));
                });
                break;
            case Constants.COMMON_TYPE.CONFIRM_ACTION:
                lookupRepository.findByLookupKey(Constants.LOOKUP_KEY.ACTION_CONFIRM).forEach(lookup -> {
                    selectionDTOS.add(populateSelectionDTO(lookup.getId(), lookup.getCode(), lookup.getName()));
                });
                break;
            case Constants.COMMON_TYPE.AVAILABLE_STATUS:
                lookupRepository.findByLookupKey(Constants.LOOKUP_KEY.AVAILABLE_STATUS).forEach(lookup -> {
                    selectionDTOS.add(populateSelectionDTO(lookup.getId(), lookup.getCode(), lookup.getName()));
                });
                break;
            case Constants.COMMON_TYPE.BRANCH:
                branchRepository.findAllByIsActive(Boolean.TRUE).forEach(branch -> {
                    selectionDTOS.add(populateSelectionDTO(branch.getId(), branch.getCode(), branch.getName()));
                });
                break;
            case Constants.COMMON_TYPE.PAYMENT_METHOD:
                lookupRepository.findByLookupKey(Constants.LOOKUP_KEY.PAYMENT_METHOD).forEach(lookup -> {
                    selectionDTOS.add(populateSelectionDTO(lookup.getId(), lookup.getCode(), lookup.getName()));
                });
                break;
        }
        responseDTO.setData(selectionDTOS);
        return responseDTO;
    }

    private SelectionDTO populateSelectionDTO(Long id, String code, String name) {
        SelectionDTO selectionDTO = new SelectionDTO();
        selectionDTO.setId(id);
        selectionDTO.setCode(code);
        selectionDTO.setName(name);
        return selectionDTO;
    }
}
