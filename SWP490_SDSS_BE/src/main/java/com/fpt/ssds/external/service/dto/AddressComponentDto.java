package com.fpt.ssds.external.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressComponentDto {

    @JsonProperty("long_name")
    private String longName;

    @JsonProperty("short_name")
    private String shortName;

    private List<String> types;
}
