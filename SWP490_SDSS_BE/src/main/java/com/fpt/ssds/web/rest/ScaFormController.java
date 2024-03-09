package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.ScaFormService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.SCAFormDto;
import com.fpt.ssds.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/sca-form")
public class ScaFormController {

    private final ScaFormService scaFormService;

    public ScaFormController(ScaFormService scaFormService) {
        this.scaFormService = scaFormService;
    }

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getAll() {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(scaFormService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(scaFormService.findById(id)));
    }

    @PostMapping("")
    public ResponseEntity<ResponseDTO> createUpdateScaForm(@RequestBody SCAFormDto scaFormDto) {
        scaFormService.createUpdate(scaFormDto);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }
}
