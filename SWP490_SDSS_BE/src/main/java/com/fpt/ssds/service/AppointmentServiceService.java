package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.AppointmentServiceDto;

import java.util.List;

public interface AppointmentServiceService {
    void cancelListService(List<AppointmentServiceDto> serviceDtos);
}
