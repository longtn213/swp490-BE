/*
package com.fpt.ssds.external.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.ssds.external.config.properties.GoogleMapsGeocodingProp;
import com.fpt.ssds.external.service.GgMapsGeoIntegrationService;
import com.fpt.ssds.external.service.dto.GeocodeResultDto;
import java.net.URLEncoder;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GgMapsGeoIntegrationServiceImpl implements GgMapsGeoIntegrationService {

    private final GoogleMapsGeocodingProp googleMapsGeocodingProp;

    @Override
    public GeocodeResultDto getGeocode(String address) {
        try {
            OkHttpClient client = new OkHttpClient();
            String encodedAddress = URLEncoder.encode(address, "UTF-8");
            Request request = new Request.Builder()
                .url(googleMapsGeocodingProp.getUrl() + encodedAddress)
                .get()
                .addHeader("x-rapidapi-host", googleMapsGeocodingProp.getHost())
                .addHeader("x-rapidapi-key", googleMapsGeocodingProp.getKey())
                .build();

            ResponseBody responseBody = client.newCall(request).execute().body();
            ObjectMapper objectMapper = new ObjectMapper();
            GeocodeResultDto result = objectMapper.readValue(responseBody.string(), GeocodeResultDto.class);
            return result;
        } catch (Exception exception) {
            return null;
        }
    }
}
*/
