package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.SMSService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/test")
@Slf4j
@RequiredArgsConstructor
public class TestController {
    private final SMSService smsService;

    @PostMapping("{phone}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable("phone") String phone) {
        smsService.sendMessage(phone);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }
}
