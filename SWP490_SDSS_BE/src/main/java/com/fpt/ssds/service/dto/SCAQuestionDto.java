package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SCAQuestionDto implements Serializable {

    private Long id;

    private String question;

    private int order;

    private Boolean isRequired;

    private Long type;

    @NotNull(message = "không để trống FormId")
    private Long formId;

    private Set<OptionDto> options = new HashSet<>();

}
