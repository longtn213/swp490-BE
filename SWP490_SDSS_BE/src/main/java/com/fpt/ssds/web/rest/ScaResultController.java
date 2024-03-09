package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.ScaResultService;
import com.fpt.ssds.service.criteria.ScaResultCriteria;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.ScaResultDto;
import com.fpt.ssds.service.queryservice.ScaResultServiceQuery;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/sca-result")
@RequiredArgsConstructor
public class ScaResultController {

    private final ScaResultService scaResultService;

    private final ScaResultServiceQuery scaResultServiceQuery;


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(scaResultService.findById(id)));
    }

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getAll(ScaResultCriteria criteria, Pageable pageable) {
        Page<ScaResultDto> page = scaResultServiceQuery.findByCriteria(criteria, pageable);
        ResponseDTO response = ResponseUtils.responseOK(page.getContent());
        response.getMeta().setTotal(page.getTotalElements());
        response.getMeta().setPage(page.getNumber());
        response.getMeta().setSize(page.getSize());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("")
    public ResponseEntity<ResponseDTO> createUpdateScaResult(@RequestBody ScaResultDto scaResultDto) {
        scaResultService.createUpdate(scaResultDto);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }
}
