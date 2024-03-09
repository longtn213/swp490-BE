package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.ErrorMessage;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link ErrorMessage} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorMessageDto implements Serializable {
    private Long id;
    private String code;
    private String returnCode;
    private String messageVi;
    private String messageEn;
}
