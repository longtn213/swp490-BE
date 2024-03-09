package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApptPerformanceByBranchDTO {
    //Doanh số
    private ApptPerformanceMetrixDTO placeGmv;

    //Tổng đơn hàng (bao gồm cả đơn bị hủy)
    private ApptPerformanceMetrixDTO placeAppointment;

    //Doanh số trên mỗi lịch hẹn
    private ApptPerformanceMetrixDTO placeSalesPerAppointment;

    //Tổng giá trị lịch hẹn đã hủy
    private ApptPerformanceMetrixDTO cancelledSales;

    //Số lượng lịch hẹn bị hủy
    private ApptPerformanceMetrixDTO cancelledAppointment;

    private ApptPerformanceMetrixDTO doneGmv;

    //Số lượng lịch hẹn bị hủy
    private ApptPerformanceMetrixDTO doneAppointment;
}
