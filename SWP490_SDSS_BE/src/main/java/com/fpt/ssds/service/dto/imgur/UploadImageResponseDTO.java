package com.fpt.ssds.service.dto.imgur;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadImageResponseDTO {
    @JsonProperty("status")
    private Integer status;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("data")
    private ImgurImageDTO data;
}
