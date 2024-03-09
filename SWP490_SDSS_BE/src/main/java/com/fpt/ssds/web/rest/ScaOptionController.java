package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.ScaOptionService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/option")
public class ScaOptionController {

    private final ScaOptionService scaOptionService;

    @Autowired
    public ScaOptionController(ScaOptionService scaOptionService) {
        this.scaOptionService = scaOptionService;
    }

    @PostMapping("delete")
    public ResponseEntity<ResponseDTO> deleteById(@RequestBody List<Long> listId) {
        scaOptionService.deleteListScaOption(listId);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }
}
