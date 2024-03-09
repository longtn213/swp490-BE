package com.fpt.ssds.service.state;

import com.fpt.ssds.domain.AppointmentService;

import java.util.List;

public interface AppointmentServiceStateListener {
    void onAppointmentServiceStateChange(AppointmentService appointmentService);

    void onAppointmentServiceStateChange(List<AppointmentService> appointmentServices);
}
