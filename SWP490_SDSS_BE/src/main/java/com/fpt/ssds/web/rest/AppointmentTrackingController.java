package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.AppointmentTrackingService;
import com.fpt.ssds.service.criteria.AppointmentTrackingCriteria;
import com.fpt.ssds.service.dto.AppointmentTrackingDto;
import com.fpt.ssds.service.dto.BookingRequestDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.queryservice.AppointmentTrackingServiceQuery;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/appointment-tracking")
@RequiredArgsConstructor
public class AppointmentTrackingController {
    private final AppointmentTrackingService appointmentTrackingService;

    private final AppointmentTrackingServiceQuery appointmentTrackingServiceQuery;

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getByCriteria(AppointmentTrackingCriteria criteria, Pageable pageable) {
        Page<AppointmentTrackingDto> page = appointmentTrackingServiceQuery.findByCriteria(criteria, pageable);
        ResponseDTO response = ResponseUtils.responseOK(page.getContent());
        response.getMeta().setTotal(page.getTotalElements());
        response.getMeta().setPage(page.getNumber());
        response.getMeta().setSize(page.getSize());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
