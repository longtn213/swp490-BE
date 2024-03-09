package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.enumeration.ReasonType;
import com.fpt.ssds.service.ReasonMessageService;
import com.fpt.ssds.service.dto.ReasonMessageDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController("PublicReasonMessageController")
@RequestMapping("${ssds.api.ref.internal}/web/v1/reason-message")
@Slf4j
@RequiredArgsConstructor
public class ReasonMessageController {
    private final ReasonMessageService reasonMessageService;

    @GetMapping("/{type}")
    public ResponseEntity<ResponseDTO> getReasonMessage(@PathVariable String type) {
        if (!ReasonType.isExisted(type)) {
            throw new SSDSBusinessException(ErrorConstants.REASON_TYPE_IS_INVALID, new ArrayList<>(Arrays.asList(type)));
        }
        List<ReasonMessageDto> lstReasonMessage = reasonMessageService.getListReasonDTOByType(type);
        ResponseDTO response = ResponseUtils.responseOK(lstReasonMessage);
        return ResponseEntity.ok().body(response);
    }
}
