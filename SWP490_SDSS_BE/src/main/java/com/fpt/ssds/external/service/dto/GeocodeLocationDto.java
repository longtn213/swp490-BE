package com.fpt.ssds.external.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodeLocationDto {

    @JsonProperty("lat")
    private String latitude;

    @JsonProperty("lng")
    private String longitude;
}
