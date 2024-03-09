package com.fpt.ssds.external.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodeObjectDto {

    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("address_components")
    private List<AddressComponentDto> addressComponents;

    @JsonProperty("formatted_address")
    private String formattedAddress;

    @JsonProperty("geometry")
    private GeocodeGeometryDto geometry;
}
