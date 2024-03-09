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
public class SCAFormDto implements Serializable {
    private Long id;

    private String name;

    private String code;

    private String description;

    private boolean active;

    @NotNull(message = "Không để trống những câu hỏi")
    private Set<SCAQuestionDto> questions = new HashSet<>();
}
