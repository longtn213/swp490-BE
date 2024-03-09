package com.fpt.ssds.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionAnswerDto implements Serializable {

    private Long id;

    private String textAnswer;

    private String textQuestion;

    private Long resultId;
}
