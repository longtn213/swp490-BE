package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.ScaQuestionService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/sca-question")
public class ScaQuestionController {
    private final ScaQuestionService scaQuestionService;

    public ScaQuestionController(ScaQuestionService scaQuestionService) {
        this.scaQuestionService = scaQuestionService;
    }

    @PostMapping("delete/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@PathVariable Long id) {
        scaQuestionService.deleteScaQuestion(id);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }
}
