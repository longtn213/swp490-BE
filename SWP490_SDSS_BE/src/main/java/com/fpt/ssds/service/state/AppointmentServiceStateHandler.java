package com.fpt.ssds.service.state;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.AppointmentService;
import com.fpt.ssds.domain.Lookup;
import com.fpt.ssds.repository.AppointmentServiceRepository;
import com.fpt.ssds.repository.LookupRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentServiceStateHandler {
    private final LookupRepository lookupRepository;

    private final AppointmentServiceRepository appointmentServiceRepository;

    private List<AppointmentServiceStateListener> listeners = new ArrayList<>();

    private final AppointmentMasterStateHandler appointmentMasterStateHandler;

    private final AuditorAware auditorAware;

    public void updateApptServiceState(AppointmentService appointmentService, String statusCode) {
        List<String> lstStatusNotUpdate = Arrays.asList(Constants.APPOINTMENT_SERVICE_STATUS.CANCELED, Constants.APPOINTMENT_SERVICE_STATUS.COMPLETED);
        if (!statusCode.equals(appointmentService.getStatus().getCode()) &&
            !lstStatusNotUpdate.contains(appointmentService.getStatus().getCode())) {
            Lookup status = lookupRepository.findByLookupKeyAndCode(Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS, statusCode).orElse(null);
            appointmentService.setStatus(status);
            updateApptServiceByStatus(appointmentService, statusCode);
            appointmentServiceRepository.save(appointmentService);
            if (CollectionUtils.isEmpty(listeners)) {
                registerApptServiceStateListener(new UpdateApptMasterApptServiceStateListener(appointmentMasterStateHandler));
            }
            notifyAppointmentServiceStateListeners(appointmentService);
        }
    }

    public void updateListApptServiceState(List<AppointmentService> appointmentServices, String statusCode) {
        List<String> lstStatusNotUpdate = Arrays.asList(Constants.APPOINTMENT_SERVICE_STATUS.CANCELED, Constants.APPOINTMENT_SERVICE_STATUS.COMPLETED);
        List<AppointmentService> lstApptServiceUpdate = new ArrayList<>();
        appointmentServices.stream().forEach(service -> {
            if (!statusCode.equals(service.getStatus().getCode())
                && !lstStatusNotUpdate.contains(service.getStatus().getCode())) {
                lstApptServiceUpdate.add(service);
            }
        });

        if (CollectionUtils.isNotEmpty(lstApptServiceUpdate)) {
            lstApptServiceUpdate.forEach(service -> {
                // tracking
                Lookup status = lookupRepository.findByLookupKeyAndCode(Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS, statusCode).orElse(null);
                service.setStatus(status);
                updateApptServiceByStatus(service, statusCode);
            });

            appointmentServiceRepository.saveAll(lstApptServiceUpdate);
            if (CollectionUtils.isEmpty(listeners)) {
                registerApptServiceStateListener(new UpdateApptMasterApptServiceStateListener(appointmentMasterStateHandler));
            }
            notifyAppointmentServiceStateListeners(lstApptServiceUpdate);
        }
    }

    private void notifyAppointmentServiceStateListeners(AppointmentService appointmentService) {
        this.listeners.forEach(listener -> listener.onAppointmentServiceStateChange(appointmentService));
    }

    private void notifyAppointmentServiceStateListeners(List<AppointmentService> appointmentServices) {
        this.listeners.forEach(listener -> listener.onAppointmentServiceStateChange(appointmentServices));
    }

    private void registerApptServiceStateListener(AppointmentServiceStateListener listener) {
        listeners.add(listener);
    }

    private void updateApptServiceByStatus(AppointmentService appointmentService, String statusCode) {
        switch (statusCode) {
            case Constants.APPOINTMENT_SERVICE_STATUS.CANCELED:
                appointmentService.setCancelBy((String) auditorAware.getCurrentAuditor().orElse("SYSTEM"));
                appointmentService.setCancelTime(Instant.now());
                break;
        }
    }
}
