package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "performance_metric")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class PerformanceMetric extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`time`")
    private Instant time;

    //Doanh số lịch hẹn đã đặt trong ngày
    @Column(name = "place_gmv")
    private Double placeGmv;

    //Tổng lịch hẹn đã đặt trong ngày
    @Column(name = "place_appointment")
    private Long placeAppointment;

    //Doanh số những lịch hẹn đã hoàn thành trong ngày
    @Column(name = "done_gmv")
    private Double doneGmv;

    //Tổng lịch hẹn đã hoàn thành trong ngày
    @Column(name = "done_appointment")
    private Long doneAppointment;

    //Doanh số lịch hẹn đã hủy trong ngày
    @Column(name = "cancelled_sales")
    private Double cancelledSales;

    @Column(name = "cancelled_appointment")
    private Long cancelledAppointment;


    @ManyToOne
    @JsonIgnoreProperties(value = "performanceMetrics", allowSetters = true)
    @JoinColumn(name = "branch_id")
    private Branch branch;
}
