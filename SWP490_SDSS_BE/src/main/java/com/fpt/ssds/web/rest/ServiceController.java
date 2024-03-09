package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.queryservice.SpaServiceQueryService;
import com.fpt.ssds.service.SpaServiceService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.service.criteria.ServiceCriteria;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/service")
@Slf4j
public class ServiceController {
    private final SpaServiceService spaServiceService;

    private final SpaServiceQueryService spaServiceQueryService;

    @Autowired
    public ServiceController(SpaServiceService spaServiceService, SpaServiceQueryService spaServiceQueryService) {
        this.spaServiceService = spaServiceService;
        this.spaServiceQueryService = spaServiceQueryService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createUpdate(@RequestBody @Valid SpaServiceDto spaServiceDto) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(spaServiceService.createUpdate(spaServiceDto)));
    }

    @GetMapping("not-assign-category")
    public ResponseEntity<ResponseDTO> getServicesNotAssignedCategory() {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(spaServiceService.getServicesNotAssignedCategory()));
    }
}
