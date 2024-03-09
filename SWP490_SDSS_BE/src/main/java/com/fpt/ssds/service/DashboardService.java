package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.ApptPerformanceDTO;
import com.fpt.ssds.service.dto.ResponseDTO;

import java.io.ByteArrayInputStream;
import java.time.ZonedDateTime;
import java.util.List;

public interface DashboardService {
    ResponseDTO getApptScorecardByTime(ZonedDateTime startTime, ZonedDateTime endTime, Long branchId);

    ResponseDTO getRevenueReportByTime(ZonedDateTime startTime, ZonedDateTime endTime, Long branchId);

    ResponseDTO getProductivityInDay(Long branchId);

    ByteArrayInputStream downloadProductivityInDay(List<Long> listBranchId);

    ByteArrayInputStream downloadRevenueByService(ZonedDateTime startTime, ZonedDateTime endTime, List<Long> listBranchId);

    ApptPerformanceDTO getAppointmentPerformance(Long startTime, Long endTime, String period, Long branchId);
}
