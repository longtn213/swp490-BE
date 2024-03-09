package com.fpt.ssds.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class PerformanceMetricDTO {
    private Long id;
    private Instant time;

    //Doanh số lịch hẹn đã đặt trong ngày
    private Double placeGmv = 0D;

    //Tổng lịch hẹn đã đặt trong ngày
    private Long placeAppointment = 0L;

    //Doanh số những lịch hẹn đã hoàn thành trong ngày
    private Double doneGmv = 0D;

    //Tổng lịch hẹn đã hoàn thành trong ngày
    private Long doneAppointment = 0L;

    //Doanh số lịch hẹn đã hủy trong ngày
    private Double cancelledSales = 0D;

    private Long cancelledAppointment = 0L;

    private Long branchId;

    public PerformanceMetricDTO(Double placeGmv, Long placeAppointment, Double doneGmv, Long doneAppointment, Double cancelledSales, Long cancelledAppointment) {
        this.placeGmv = placeGmv;
        this.placeAppointment = placeAppointment;
        this.doneGmv = doneGmv;
        this.doneAppointment = doneAppointment;
        this.cancelledSales = cancelledSales;
        this.cancelledAppointment = cancelledAppointment;
    }
}
