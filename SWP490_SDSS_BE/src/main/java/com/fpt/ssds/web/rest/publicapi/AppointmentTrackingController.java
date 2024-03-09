package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.service.AppointmentTrackingService;
import com.fpt.ssds.service.dto.BookingRequestDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.queryservice.AppointmentTrackingServiceQuery;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("PublicAppointmentTrackingController")
@RequestMapping("${ssds.api.ref.public}/web/v1/appointment-tracking")
@RequiredArgsConstructor
public class AppointmentTrackingController {
    private final AppointmentTrackingService appointmentTrackingService;

    private final AppointmentTrackingServiceQuery appointmentTrackingServiceQuery;

    @PostMapping("/available-time")
    public ResponseEntity<ResponseDTO> getAvailableTimeByBranch(@RequestBody BookingRequestDto bookingRequestDto) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(appointmentTrackingService.
            getAvailableTimeByBranch(bookingRequestDto)));
    }
}
