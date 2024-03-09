package com.fpt.ssds.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class ConfirmAppointmentRequestDto implements Serializable {
    @NotNull(message = "Không được để trống thông mã lịch hẹn")
    private Long apptMasterId;
    @NotNull(message = "Vui lòng lựa chọn hành động để tiếp tục")
    private String action;
    private ReasonMessageDto canceledReason;
    private String note;
}
