package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.AppointmentTracking;
import com.fpt.ssds.service.dto.AppointmentTrackingDto;
import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.service.dto.BranchDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Branch} and its DTO {@link BranchDto}.
 */
@Mapper(componentModel = "spring", uses = {BranchMapper.class})
public interface AppointmentTrackingMapper extends EntityMapper<AppointmentTrackingDto, AppointmentTracking> {
    @Override
    @Mapping(source = "branchId", target = "branch")
    AppointmentTracking toEntity(AppointmentTrackingDto dto);

    @Override
    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "branch.name", target = "branchName")
    AppointmentTrackingDto toDto(AppointmentTracking entity);

    default AppointmentTracking fromId(Long id) {
        if (id == null) {
            return null;
        }
        AppointmentTracking apptTracking = new AppointmentTracking();
        apptTracking.setId(id);
        return apptTracking;
    }
}
