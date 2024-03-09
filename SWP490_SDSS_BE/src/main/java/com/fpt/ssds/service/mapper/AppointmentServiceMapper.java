package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.AppointmentService;
import com.fpt.ssds.service.dto.AppointmentServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link AppointmentService} and its DTO {@link AppointmentServiceDto}.
 */
@Mapper(componentModel = "spring", uses = {SpaServiceMapper.class})
public interface AppointmentServiceMapper extends EntityMapper<AppointmentServiceDto, AppointmentService> {
    @Override
    @Mapping(source = "spaServiceId", target = "spaService")
    AppointmentService toEntity(AppointmentServiceDto dto);

    @Override
    @Mapping(source = "spaService.id", target = "spaServiceId")
    @Mapping(source = "spaService.currentPrice", target = "spaServicePrice")
    @Mapping(source = "spaService.code", target = "spaServiceCode")
    @Mapping(source = "spaService.name", target = "spaServiceName")
    @Mapping(source = "spaService.duration", target = "duration")
    AppointmentServiceDto toDto(AppointmentService entity);

    default AppointmentService fromId(Long id) {
        if (id == null) {
            return null;
        }
        AppointmentService appointmentService = new AppointmentService();
        appointmentService.setId(id);
        return appointmentService;
    }
}
