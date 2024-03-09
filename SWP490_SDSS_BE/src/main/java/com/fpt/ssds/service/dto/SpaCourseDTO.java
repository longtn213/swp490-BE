package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link SpaCourse} entity
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaCourseDTO {

    private Long id;

    private Instant startTime;

    private Instant endTime;

    private Instant cancelTime;

    private String cancelBy;

    @NotNull(message = "Vui lòng không để trống thông tin chi nhánh.")
    private BranchDto branch;

    private LookupDto status;

    @NotNull(message = "Vui lòng không để trống thông tin khách hàng")
    private UserDto customer;

    @NotNull(message = "Vui lòng thêm thông tin các buổi thực hiện dịch vụ cho liệu trình.")
    private Set<Session> sessions = new HashSet<>();
}
