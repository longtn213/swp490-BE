package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.DashboardService;
import com.fpt.ssds.service.dto.ApptPerformanceDTO;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.DateUtils;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/dashboard")
@Slf4j
@RequiredArgsConstructor
public class DashboardController {
    @Value("${ssds.config.timezone}")
    String systemTimezone;

    private final DashboardService dashboardService;

    @GetMapping("appt-scorecard")
    public ResponseEntity<ResponseDTO> getApptScorecardByTime(@RequestParam(required = false, name = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime startTime,
                                                              @RequestParam(required = false, name = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime endTime,
                                                              @RequestParam(name = "branchId") Long branchId) {
        return ResponseEntity.ok(dashboardService.getApptScorecardByTime(startTime, endTime, branchId));
    }

    @GetMapping("revenue-report")
    public ResponseEntity<ResponseDTO> getRevenueReportByTime(@RequestParam(required = false, name = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime startTime,
                                                              @RequestParam(required = false, name = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime endTime,
                                                              @RequestParam(name = "branchId") Long branchId) {
        return ResponseEntity.ok(dashboardService.getRevenueReportByTime(startTime, endTime, branchId));
    }

    @GetMapping("productivity-in-day")
    public ResponseEntity<ResponseDTO> getProductivityInDay(@RequestParam(name = "branchId") Long branchId) {
        return ResponseEntity.ok(dashboardService.getProductivityInDay(branchId));
    }

    @PostMapping("download-productivity-in-day")
    public ResponseEntity<InputStreamResource> downloadProductivityInDay(@RequestBody List<Long> listBranchId) {
        String filename = "Nang_suat_trong_ngay_" + DateUtils.formatInstantToString(Instant.now(), systemTimezone, "dd_MM_yyyy_hh_mm_ss") + ".xlsx";
        InputStreamResource file = new InputStreamResource(dashboardService.downloadProductivityInDay(listBranchId));

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .body(file);
    }

    @PostMapping("download-revenue-by-service")
    public ResponseEntity<InputStreamResource> downloadRevenueByService(@RequestParam(required = false, name = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime startTime,
                                                                        @RequestParam(required = false, name = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime endTime,
                                                                        @RequestBody List<Long> listBranchId) {
        String filename = "Doanh_thu_theo_dich_vu_" + DateUtils.formatInstantToString(Instant.now(), systemTimezone, "dd_MM_yyyy_hh_mm_ss") + ".xlsx";
        InputStreamResource file = new InputStreamResource(dashboardService.downloadRevenueByService(startTime, endTime, listBranchId));

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .body(file);
    }

    @GetMapping("/appointment-performance")
    public ResponseEntity<ResponseDTO> getAppointmentPerformance(@RequestParam(name = "startTime") Long startTime,
                                                                 @RequestParam(name = "endTime") Long endTime,
                                                                 @RequestParam(name = "period") String period,
                                                                 @RequestParam(name = "branchId") Long branchId
    ) {
        ApptPerformanceDTO appointmentPerformance = dashboardService.getAppointmentPerformance(startTime, endTime, period, branchId);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(appointmentPerformance));
    }

}
