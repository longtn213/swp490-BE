package com.fpt.ssds.service;

import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.domain.User;
import com.fpt.ssds.service.dto.AppointmentMasterDto;
import com.fpt.ssds.service.dto.ConfirmAppointmentRequestDto;
import com.fpt.ssds.service.dto.ResponseDTO;

import java.util.List;

public interface AppointmentMasterService {
    AppointmentMaster createUpdate(AppointmentMasterDto appointmentMasterDto);

    AppointmentMasterDto getById(Long apptMasterId, User user);

    AppointmentMaster findById(Long apptMasterId);

    void confirmAppointments(List<ConfirmAppointmentRequestDto> confirmAppointmentRequestDtos);

    void cancelAppointmentMaster(List<AppointmentMasterDto> appointmentMasterDtos);

    ResponseDTO checkin(AppointmentMaster appointmentMaster, AppointmentMasterDto appointmentMasterDto);

    ResponseDTO checkout(AppointmentMaster appointmentMaster, AppointmentMasterDto appointmentMasterDto);

    void sendConfirmMessage(Long amId, String branchCode);

    void setOverdueInfo(Long branchId);
}
