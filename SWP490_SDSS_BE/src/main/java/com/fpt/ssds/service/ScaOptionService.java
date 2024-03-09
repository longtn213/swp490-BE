package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.OptionDto;

import java.util.List;
import java.util.Set;

public interface ScaOptionService {

    void createUpdateOption(Set<OptionDto> optionDtoList);

    void deleteListScaOption(List<Long> listId);

}
