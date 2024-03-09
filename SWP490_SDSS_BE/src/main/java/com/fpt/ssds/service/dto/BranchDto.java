package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.Branch;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for the {@link Branch} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchDto implements Serializable {
    private Long id;
    @NotNull(message = "Không được để trống tên cơ sở")
    private String name;
    private String code;
    @NotNull(message = "Không được để trống địa chỉ cơ sở")
    private String detailAddress;
    private Double latitude;
    private Double longitude;
    @NotNull(message = "Không được để trống số điện thoại")
    private String hotline;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
    private String country = "VN";
    @NotNull(message = "Không được để trống thông tin tỉnh/thành")
    private LocationDTO state;
    @NotNull(message = "Không được để trống thông tin quận/huyện")
    private LocationDTO city;
    @NotNull(message = "Không được để trống thông tin phường/xã")
    private LocationDTO district;
    private List<FileDto> files = new ArrayList<>();
    private Boolean isActive;
}
