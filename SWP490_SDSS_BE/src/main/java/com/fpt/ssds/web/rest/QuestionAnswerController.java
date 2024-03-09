package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.QuestionAnswerService;
import com.fpt.ssds.service.dto.QuestionAnswerDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/question-answer")
public class QuestionAnswerController {

    private final QuestionAnswerService questionAnswerService;

    public QuestionAnswerController(QuestionAnswerService questionAnswerService) {
        this.questionAnswerService = questionAnswerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(questionAnswerService.findById(id)));
    }

}
