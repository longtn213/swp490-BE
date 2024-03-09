package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Auth0UserLoginRequestDTO {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("grant_type")
    private String grantType = "password";
    @JsonProperty("content_type")
    private String contentType = "application/x-www-form-urlencoded";
    @JsonProperty("audience")
    private String audience;
}
