package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.AppointmentMaster;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for the {@link AppointmentMaster} entity
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class AppointmentMasterDto {
    private Long id;

    @NotNull(message = "Không được để trống thông tin thời gian bắt đầu dự kiến")
    private Instant expectedStartTime;

    private Instant expectedEndTime;
    private Instant actualStartTime;
    private Instant actualEndTime;
    private Instant cancelTime;
    private String cancelBy;
    private Double total;
    private Double payAmount;
    @NotNull(message = "Không được để trống thông tin chi nhánh")
    private Long branchId;
    private String branchCode;
    private String branchName;
    private Long sessionId;
    @NotNull(message = "Không được để trống thông tin khách hàng")
    private UserListingDTO customer;
    @NotNull(message = "Không được để trống thông tin dịch vụ")
    @Valid
    private List<AppointmentServiceDto> appointmentServices;
    private LookupDto status;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
    private Boolean requireConfirm = true;
    private ReasonMessageDto canceledReason;
    private String note;
    private String overdueStatus;
    private String statusCode;
    private List<FileDto> invoices = new ArrayList<>();
    private List<FileDto> imgBefore = new ArrayList<>();
    private List<FileDto> imgAfter = new ArrayList<>();
    private String paymentMethod;

    public AppointmentMasterDto(Long id,
                                Instant expectedStartTime,
                                Instant expectedEndTime,
                                Instant actualStartTime,
                                Instant actualEndTime,
                                Instant cancelTime,
                                Double total,
                                Double payAmount,
                                Long branchId,
                                String branchCode,
                                String branchName,
                                String statusCode) {
        this.id = id;
        this.expectedStartTime = expectedStartTime;
        this.expectedEndTime = expectedEndTime;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
        this.cancelTime = cancelTime;
        this.total = total;
        this.payAmount = payAmount;
        this.branchId = branchId;
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.statusCode = statusCode;
    }
}
