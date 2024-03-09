package com.fpt.ssds.service.impl;

import com.fpt.ssds.service.ApiService;
import com.fpt.ssds.service.dto.Auth0RequestDTO;
import com.fpt.ssds.service.dto.Auth0ResponseDTO;
import com.fpt.ssds.utils.DateUtils;
import com.fpt.ssds.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Objects;

@Service
public class ApiServiceImpl implements ApiService {
    private final Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    @Value("${ssds.config.auth0.base-domain}")
    String auth0BaseDomain;

    @Value("${ssds.config.auth0.client-id}")
    String auth0ClientId;

    @Value("${ssds.config.auth0.client-secret}")
    String auth0ClientSecret;

    @Value("${ssds.config.auth0.audience}")
    String auth0Audience;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    public static String TOKEN_AUTH0;
    public static String TOKEN_TYPE;
    public static Date EXPIRED_TOKEN;

    @Override
    public Object postRequest(String url, HttpHeaders headers, Object payload, Class<?> responseClass) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> request = new HttpEntity<>(payload, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, responseClass);
    }

    @Override
    public Object patchRequest(String url, HttpHeaders headers, Object payload, Class<?> responseClass) {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        HttpEntity<Object> request = new HttpEntity<>(payload, headers);
        return restTemplate.exchange(url, HttpMethod.PATCH, request, responseClass);
//        return restTemplate.patchForObject(url, request, responseClass);
    }

    @Override
    public HttpHeaders getHeaderAuth0() {
        String tokenAuth = getTokenAuth0();
        if (StringUtils.isEmpty(tokenAuth)) {
            logger.error("tokenAuth0 is isNullOrEmpty");
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", tokenAuth);
        return headers;
    }

    private String getTokenAuth0() {
        try {
            if (StringUtils.isEmpty(TOKEN_AUTH0)) {
                Auth0ResponseDTO response = callAPIGetTokenAuth0();
                if (Objects.isNull(response) || StringUtils.isEmpty(response.getAccessToken())) {
                    logger.error("getTokenAuth0 error. ");
                    resetToken();
                    return null;
                }
                EXPIRED_TOKEN = DateUtils.addSecond(new Date(), response.getExpiresIn() - 500);
                TOKEN_AUTH0 = response.getAccessToken();
                TOKEN_TYPE = response.getTokenType();
            } else {
                if (EXPIRED_TOKEN.before(new Date())) {
                    Auth0ResponseDTO response = callAPIGetTokenAuth0();
                    if (Objects.isNull(response) || StringUtils.isEmpty(response.getAccessToken())) {
                        resetToken();
                        return null;
                    }
                    EXPIRED_TOKEN = DateUtils.addSecond(new Date(), response.getExpiresIn() - 500);
                    TOKEN_AUTH0 = response.getAccessToken();
                    TOKEN_TYPE = response.getTokenType();
                }
            }
            if (Objects.isNull(TOKEN_TYPE) || StringUtils.isEmpty(TOKEN_AUTH0)) {
                resetToken();
                return null;
            }
            return TOKEN_TYPE + " " + TOKEN_AUTH0;

        } catch (Exception e) {
            logger.error("getTokenAuth0 Exception: ", e);
            resetToken();
            return null;
        }
    }

    private Auth0ResponseDTO callAPIGetTokenAuth0() throws Exception {
        if (StringUtils.isEmpty(auth0ClientId) || StringUtils.isEmpty(auth0ClientSecret)) {
            logger.error("clientId or clientSecret not exist");
            return null;
        }
        String urlAuth = auth0BaseDomain + "/oauth/token";
        String grantType = "client_credentials";
        Auth0RequestDTO requestAuth = new Auth0RequestDTO();
        requestAuth.setClientId(auth0ClientId);
        requestAuth.setClientSecret(auth0ClientSecret);
        requestAuth.setAudience(auth0Audience);
        requestAuth.setGrantType(grantType);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        HttpEntity<Auth0RequestDTO> request = new HttpEntity<>(requestAuth, headers);
        ResponseEntity<String> response = restTemplate.exchange(urlAuth, HttpMethod.POST, request, String.class);
        if (response.getStatusCode().value() != 200) {
            logger.error("response callAPIGetTokenAuth0 Error: " + response.getBody());
            return null;
        }
        return Utils.convertJsonStringToObject(response.getBody(), Auth0ResponseDTO.class);
    }

    @Override
    public void resetToken() {
        TOKEN_TYPE = null;
        TOKEN_AUTH0 = null;
    }

    @Override
    public Object postRequest(String url, Object payload, Class<?> responseClass) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> request = new HttpEntity<>(payload);
        return restTemplate.exchange(url, HttpMethod.POST, request, responseClass);
    }
}
