package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.SpaCourseService;
import com.fpt.ssds.service.dto.AppointmentMasterDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.SpaCourseDTO;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/spa-course")
@Slf4j
@RequiredArgsConstructor
public class SpaCourseController {
    private final SpaCourseService spaCourseService;

    @PostMapping("")
    public ResponseEntity<ResponseDTO> createUpdate(@RequestBody @Valid SpaCourseDTO spaCourseDTO) {
        spaCourseService.createUpdate(spaCourseDTO);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }
}
