package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Auth0ChangePasswordRequestDTO {
    private String password;
    private String connection;
}
