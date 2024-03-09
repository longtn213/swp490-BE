/*
package com.fpt.ssds.external.rest;

import com.fpt.ssds.external.service.GgMapsGeoIntegrationService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${ssds.api.ref.external}/v1/google-map-geocode")
@RequiredArgsConstructor
public class GGMapsGeoController {

    private final GgMapsGeoIntegrationService service;

    @GetMapping
    public ResponseEntity<ResponseDTO> getGeocode(@RequestParam String address, HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        return ResponseEntity.ok().body(ResponseUtils.responseOK(service.getGeocode(address)));
    }
}
*/
