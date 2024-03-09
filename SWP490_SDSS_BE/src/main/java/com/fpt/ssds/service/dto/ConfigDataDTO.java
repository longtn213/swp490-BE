package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigDataDTO {
    private Long id;

    private String configKey;

    private String configValue;

    private String configDesc;

    private Long branchId;

    private String branchName;

    private String type;

    private String allowUpdate;

    private Boolean autoByPassWarning = false;
}
