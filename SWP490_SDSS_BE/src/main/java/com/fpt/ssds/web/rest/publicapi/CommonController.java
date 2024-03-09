package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.service.CommonService;
import com.fpt.ssds.service.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("PublicCommonController")
@RequestMapping("${ssds.api.ref.public}/web/v1/common")
public class CommonController {
    private final Logger log = LoggerFactory.getLogger(CommonController.class);

    private final CommonService commonService;

    @Autowired
    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    @GetMapping("/list-selection/{type}")
    public ResponseEntity<ResponseDTO> getListSelection(@PathVariable String type) {
        ResponseDTO response = commonService.getSelectionByType(type);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
