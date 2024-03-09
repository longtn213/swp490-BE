package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth0CreateUserResponseDTO {
    // success response
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("email")
    private String email;
    @JsonProperty("name")
    private String name;
    @JsonProperty("nickname")
    private String nickname;
    // fail ressponse
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("error")
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
}
