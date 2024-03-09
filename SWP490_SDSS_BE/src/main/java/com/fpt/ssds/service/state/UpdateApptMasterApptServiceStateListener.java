package com.fpt.ssds.service.state;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.domain.AppointmentService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UpdateApptMasterApptServiceStateListener implements AppointmentServiceStateListener {
    private final AppointmentMasterStateHandler appointmentMasterStateHandler;


    @Override
    public void onAppointmentServiceStateChange(AppointmentService appointmentService) {
        if (Objects.nonNull(appointmentService.getAppointmentMaster())) {
            updateApptMasterStatus(appointmentService.getAppointmentMaster(), appointmentService.getStatus().getCode());
        }

    }

    @Override
    public void onAppointmentServiceStateChange(List<AppointmentService> appointmentServices) {
        String statusCode = appointmentServices.get(0).getStatus().getCode();
        List<Long> lstApptServiceId = appointmentServices.stream().map(AppointmentService::getId).collect(Collectors.toList());
        List<AppointmentMaster> appointmentMasters = appointmentMasterStateHandler.findAllApptMasterByServicesId(lstApptServiceId);
        appointmentMasters.forEach(appointmentMaster ->
            updateApptMasterStatus(appointmentMaster, statusCode));
    }

    private void updateApptMasterStatus(AppointmentMaster appointmentMaster, String apptServiceStatusCode) {
        switch (apptServiceStatusCode) {
            case Constants.APPOINTMENT_SERVICE_STATUS.READY:
                if (appointmentMasterStateHandler.checkAllServicesReady(appointmentMaster.getId())) {
                    appointmentMasterStateHandler.updateApptMasterState(appointmentMaster, Constants.APPOINTMENT_MASTER_STATUS.READY);
                }
                break;
            case Constants.APPOINTMENT_SERVICE_STATUS.IN_PROGRESS:
                appointmentMasterStateHandler.updateApptMasterState(appointmentMaster, Constants.APPOINTMENT_MASTER_STATUS.IN_PROGRESS);
                break;
            case Constants.APPOINTMENT_SERVICE_STATUS.CANCELED:
                if (appointmentMasterStateHandler.checkAllServicesCancelled(appointmentMaster.getId())) {
                    appointmentMasterStateHandler.updateApptMasterState(appointmentMaster, Constants.APPOINTMENT_MASTER_STATUS.CANCELED);
                } else if (appointmentMasterStateHandler.checkAllServicesCancelledAndCompleted(appointmentMaster.getId())) {
                    appointmentMasterStateHandler.updateApptMasterState(appointmentMaster, Constants.APPOINTMENT_MASTER_STATUS.COMPLETED);
                }
                break;
        }
    }
}
