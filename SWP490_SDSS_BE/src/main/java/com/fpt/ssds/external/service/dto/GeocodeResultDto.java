package com.fpt.ssds.external.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodeResultDto {

    @JsonProperty("results")
    private List<GeocodeObjectDto> results;

    @JsonProperty("status")
    private String status;
}
