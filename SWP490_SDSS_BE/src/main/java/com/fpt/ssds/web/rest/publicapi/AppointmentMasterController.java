package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.service.AppointmentMasterService;
import com.fpt.ssds.service.dto.AppointmentMasterDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController("PublicAppointmentMasterController")
@RequestMapping("${ssds.api.ref.public}/web/v1/appointment-master")
@RequiredArgsConstructor
public class AppointmentMasterController {
    private final AppointmentMasterService appointmentMasterService;

    @PostMapping("")
    public ResponseEntity<ResponseDTO> create(@RequestBody @Valid AppointmentMasterDto appointmentMasterDto) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(appointmentMasterService.createUpdate(appointmentMasterDto)));
    }
}
