package com.fpt.ssds.web.rest;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.User;
import com.fpt.ssds.service.AppointmentTrackingService;
import com.fpt.ssds.service.ConfigDataService;
import com.fpt.ssds.service.dto.WarningResponseDTO;
import com.fpt.ssds.service.queryservice.ConfigDataServiceQuery;
import com.fpt.ssds.service.dto.ConfigDataDTO;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.criteria.ConfigDataCriteria;
import com.fpt.ssds.utils.HTTPUtils;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.StringFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

import static com.fpt.ssds.constant.Constants.COMMON.USER;


@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/config")
@Slf4j
@RequiredArgsConstructor
public class ConfigDataController {

    private final ConfigDataServiceQuery configDataServiceQuery;

    private final ConfigDataService configDataService;

    private final AppointmentTrackingService appointmentTrackingService;

    @GetMapping("{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ResponseUtils.responseOK(configDataService.getById(id)));
    }

    @PostMapping()
    public ResponseEntity<ResponseDTO> updateConfigData(@RequestBody ConfigDataDTO configDataDTO) {
        WarningResponseDTO warningResponseDTO = configDataService.updateConfig(configDataDTO);
        return ResponseEntity.ok(ResponseUtils.responseOK(warningResponseDTO));
    }
}
