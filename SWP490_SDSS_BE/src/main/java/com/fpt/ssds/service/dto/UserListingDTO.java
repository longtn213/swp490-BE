package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserListingDTO {
    private Long id;
    private String fullName;
    @NotNull(message = "Không được để trống số điện thoại khách hàng")
    private String phoneNumber;
    @NotNull(message = "Không được để trống tên đăng nhập")
    private String username;
    @NotNull(message = "Không được để trống mật khẩu")
    private String password;
    private Instant dob;
    private Boolean gender;
    @NotNull(message = "Không được để trống email")
    private String email;
    private Boolean isActive;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
}
