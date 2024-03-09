package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.AppointmentServiceService;
import com.fpt.ssds.service.dto.AppointmentMasterDto;
import com.fpt.ssds.service.dto.AppointmentServiceDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/appointment-service")
@RequiredArgsConstructor
public class AppointmentServiceController {
    private final AppointmentServiceService appointmentServiceService;

    @PostMapping("/cancel")
    public ResponseEntity<ResponseDTO> cancelAppointmentMaster(@RequestBody List<AppointmentServiceDto> serviceDtos) {
        appointmentServiceService.cancelListService(serviceDtos);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }
}
