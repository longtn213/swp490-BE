package com.fpt.ssds.service.state;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.domain.Lookup;
import com.fpt.ssds.repository.AppointmentMasterRepository;
import com.fpt.ssds.repository.LookupRepository;
import com.fpt.ssds.service.AppointmentTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentMasterStateHandler {
    private final LookupRepository lookupRepository;

    private final AppointmentMasterRepository appointmentMasterRepository;

    private final AppointmentTrackingService appointmentTrackingService;

    private final AuditorAware auditorAware;

    public void updateApptMasterState(AppointmentMaster appointmentMaster, String statusCode) {
        List<String> lstStatusNotUpdate = Arrays.asList(Constants.APPOINTMENT_MASTER_STATUS.CANCELED, Constants.APPOINTMENT_MASTER_STATUS.CLOSED);
        if (!statusCode.equals(appointmentMaster.getStatus().getCode()) ||
            !lstStatusNotUpdate.contains(appointmentMaster.getStatus().getCode())) {
            Lookup status = lookupRepository.findByLookupKeyAndCode(Constants.LOOKUP_KEY.APPOINTMENT_MASTER_STATUS, statusCode).orElse(null);
            appointmentMaster.setStatus(status);
            updateApptMasterByStatus(appointmentMaster, statusCode);
            appointmentMasterRepository.save(appointmentMaster);
        }
    }

    private void updateApptMasterByStatus(AppointmentMaster appointmentMaster, String statusCode) {
        switch (statusCode) {
            case Constants.APPOINTMENT_MASTER_STATUS.CANCELED:
                appointmentMaster.setCancelBy((String) auditorAware.getCurrentAuditor().orElse("SYSTEM"));
                appointmentMaster.setCancelTime(Instant.now());
                appointmentTrackingService.minusBookedQtyBetweenTime(appointmentMaster.getExpectedStartTime(), appointmentMaster.getExpectedEndTime(), appointmentMaster.getBranch().getId());
                break;
        }
    }

    public Boolean checkAllServicesReady(Long apptMasterId) {
        return appointmentMasterRepository.countByServiceStatusNotIn(apptMasterId, Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS, Arrays.asList(Constants.APPOINTMENT_SERVICE_STATUS.READY)) == 0;
    }

    public List<AppointmentMaster> findAllApptMasterByServicesId(List<Long> lstApptServiceId) {
        return appointmentMasterRepository.findAllByServiceId(lstApptServiceId);
    }

    public boolean checkAllServicesCancelled(Long apptMasterId) {
        return appointmentMasterRepository.countByServiceStatusNotIn(apptMasterId, Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS, Arrays.asList(Constants.APPOINTMENT_SERVICE_STATUS.CANCELED)) == 0;
    }

    public boolean checkAllServicesCancelledAndCompleted(Long apptMasterId) {
        return appointmentMasterRepository.countByServiceStatusNotIn(apptMasterId, Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS, Arrays.asList(Constants.APPOINTMENT_SERVICE_STATUS.CANCELED, Constants.APPOINTMENT_SERVICE_STATUS.COMPLETED)) == 0;
    }
}
