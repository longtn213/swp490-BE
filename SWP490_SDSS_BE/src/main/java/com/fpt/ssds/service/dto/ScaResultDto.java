package com.fpt.ssds.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ScaResultDto implements Serializable {

    private Long id;

    private String comment;

    private UserDto customer;

    private UserDto repliedBy;

    private LookupDto status;

    private List<QuestionAnswerDto> answerSet;

    private List<SpaServiceDto> spaServices;

    private List<FileDto> files = new ArrayList<>();

    private Instant createdDate;

}
