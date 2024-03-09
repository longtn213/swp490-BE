package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Auth0CreateUserRequestDTO {
    @JsonProperty("verify_email")
    private Boolean verifyEmail;
    @JsonProperty("email_verified")
    private Boolean emailVerified;
    @JsonProperty("email")
    private String email;
    @JsonProperty("username")
    private String username;
    @JsonProperty("connection")
    private String connection;
    @JsonProperty("password")
    private String password;
    @JsonProperty("client_id")
    private String clientId;
}
