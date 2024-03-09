package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.Auth0CreateUserRequestDTO;
import com.fpt.ssds.service.dto.Auth0CreateUserResponseDTO;
import org.springframework.http.HttpHeaders;

public interface ApiService {
    Object postRequest(String url, HttpHeaders headers, Object payload, Class<?> responseClass);

    Object patchRequest(String url, HttpHeaders headers, Object payload, Class<?> responseClass);

    public HttpHeaders getHeaderAuth0();

    public void resetToken();

    Object postRequest(String url, Object payload, Class<?> responseClass);
}
