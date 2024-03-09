package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldErrorDTO {
    private static final long serialVersionUID = 1L;

    private String field;

    private String message;

    private Long objectId;

    public FieldErrorDTO(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public FieldErrorDTO(Long objectId, String field, String message) {
        this.objectId = objectId;
        this.field = field;
        this.message = message;
    }
}
