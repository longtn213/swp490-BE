package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * A DTO for the {@link User} entity
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private String fullName;
    @NotEmpty(message = "Vui lòng không để trống số điện thoại.")
    private String phoneNumber;
    @NotEmpty(message = "Vui lòng không để trống tên đăng nhập.")
    private String username;
    @NotEmpty(message = "Vui lòng không để trống mật khẩu.")
    private String password;
    private Instant dob;
    private Boolean gender;
    private String email;
    private Boolean isActive;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
    private BranchDto branch;
    private RoleDto role;
    private String newPassword;
    private FileDto avatar;
}
