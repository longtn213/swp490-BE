package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.domain.AppointmentService;
import com.fpt.ssds.service.dto.AppointmentMasterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link AppointmentMaster} and its DTO {@link AppointmentMasterDto}.
 */
@Mapper(componentModel = "spring", uses = {BranchMapper.class, UserMapper.class, AppointmentServiceMapper.class, LookupMapper.class, ReasonMessageMapper.class})
public interface AppointmentMasterMapper extends EntityMapper<AppointmentMasterDto, AppointmentMaster> {
    @Override
    @Mapping(source = "branchId", target = "branch")
        /*@Mapping(source = "sessionId", target = "session")*/
    AppointmentMaster toEntity(AppointmentMasterDto dto);

    @Override
    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "branch.code", target = "branchCode")
    @Mapping(source = "branch.name", target = "branchName")
    AppointmentMasterDto toDto(AppointmentMaster entity);

    default AppointmentMaster fromId(Long id) {
        if (id == null) {
            return null;
        }
        AppointmentMaster appointmentMaster = new AppointmentMaster();
        appointmentMaster.setId(id);
        return appointmentMaster;
    }
}
