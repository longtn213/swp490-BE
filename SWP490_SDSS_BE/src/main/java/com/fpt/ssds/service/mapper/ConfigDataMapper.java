package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.ConfigData;
import com.fpt.ssds.service.dto.ConfigDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BranchMapper.class})
public interface ConfigDataMapper extends EntityMapper<ConfigDataDTO, ConfigData> {
    @Override
    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "branch.name", target = "branchName")
    ConfigDataDTO toDto(ConfigData entity);

    @Mapping(source = "branchId", target = "branch")
    ConfigData toEntity(ConfigDataDTO configDataDTO);

    default ConfigData fromId(Long id) {
        if (id == null) {
            return null;
        }
        ConfigData configData = new ConfigData();
        configData.setId(id);
        return configData;
    }
}
