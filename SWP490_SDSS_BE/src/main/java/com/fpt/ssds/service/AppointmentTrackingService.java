package com.fpt.ssds.service;

import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.service.dto.BookingRequestDto;

import java.time.Instant;
import java.util.List;

public interface AppointmentTrackingService {
    void autoCreateData(Long branchId);

    List<Instant> getAvailableTimeByBranch(BookingRequestDto bookingRequestDto);

    void plusBookedQuantity(AppointmentMaster appointmentMaster);

    void minusBookedQtyBetweenTime(Instant expectedStartTime, Instant expectedEndTime, Long branchId);
}
