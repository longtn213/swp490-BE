package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.domain.AppointmentService;
import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.domain.PerformanceMetric;
import com.fpt.ssds.repository.AppointmentMasterRepository;
import com.fpt.ssds.repository.AppointmentServiceRepository;
import com.fpt.ssds.repository.BranchRepository;
import com.fpt.ssds.repository.PerformanceMetricRepository;
import com.fpt.ssds.service.BranchService;
import com.fpt.ssds.service.DashboardService;
import com.fpt.ssds.service.ExcelHelper;
import com.fpt.ssds.service.dto.*;
import com.fpt.ssds.utils.DateUtils;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    @Value("${ssds.config.timezone}")
    String systemTimezone;

    private final AppointmentMasterRepository appointmentMasterRepository;

    private final AppointmentServiceRepository appointmentServiceRepository;

    private final BranchService branchService;

    private final BranchRepository branchRepository;

    private final ExcelHelper excelHelper;

    private final PerformanceMetricRepository performanceMetricRepository;

    @Override
    public ResponseDTO getApptScorecardByTime(ZonedDateTime startTime, ZonedDateTime endTime, Long branchId) {
        ResponseDTO responseDTO = ResponseUtils.responseOK(null);
        Map<String, Instant> instantMap = DateUtils.zonedDateTimeToInstant(startTime, endTime, systemTimezone);
        Instant instantStartTime = instantMap.get("startTime");
        Instant instantEndTime = instantMap.get("endTime");

        branchService.findById(branchId);
        ApptScorecardDTO apptScorecardDTO = new ApptScorecardDTO();
        apptScorecardDTO.setTotalWaitForConfirm(appointmentMasterRepository.countAmByCreatedDateBetweenAndStatusCodeAndBranchId(instantStartTime, instantEndTime, Constants.APPOINTMENT_MASTER_STATUS.WAITING_FOR_CONFIRMATION, branchId));
        apptScorecardDTO.setTotalReady(appointmentMasterRepository.countAmByCreatedDateBetweenAndStatusCodeAndBranchId(instantStartTime, instantEndTime, Constants.APPOINTMENT_MASTER_STATUS.READY, branchId));
        apptScorecardDTO.setTotalInprocess(appointmentMasterRepository.countAmByActualStartTimeBetweenAndStatusCodeAndBranchId(instantStartTime, instantEndTime, Constants.APPOINTMENT_MASTER_STATUS.IN_PROGRESS, branchId));
        apptScorecardDTO.setTotalClosed(appointmentMasterRepository.countByActualEndTimeBetweenAndStatusCodeAndBranchId(instantStartTime, instantEndTime, Constants.APPOINTMENT_MASTER_STATUS.CLOSED, branchId));
        apptScorecardDTO.setTotalCanceled(appointmentMasterRepository.countByCancelTimeBetweenAndStatusCodeAndBranchId(instantStartTime, instantEndTime, Constants.APPOINTMENT_MASTER_STATUS.CANCELED, branchId));
        apptScorecardDTO.setTotalOverdue(appointmentMasterRepository.countOverdueApptBetweenTimeAndBranchId(instantStartTime, instantEndTime, Constants.OVERDUE_STATUS.OVERDUE, branchId));
        responseDTO.setData(apptScorecardDTO);
        return responseDTO;
    }

    @Override
    @Transactional
    public ResponseDTO getRevenueReportByTime(ZonedDateTime startTime, ZonedDateTime endTime, Long branchId) {
        ResponseDTO responseDTO = ResponseUtils.responseOK(null);
        Map<String, Instant> instantMap = DateUtils.zonedDateTimeToInstant(startTime, endTime, systemTimezone);
        Instant instantStartTime = instantMap.get("startTime");
        Instant instantEndTime = instantMap.get("endTime");
        branchService.findById(branchId);

        List<AppointmentMaster> ams = appointmentMasterRepository.findByActualEndTimeBetweenAndBranchIdAndStatusCode(instantStartTime, instantEndTime, branchId, Constants.APPOINTMENT_MASTER_STATUS.CLOSED);
//        List<AppointmentService> apptServices = appointmentServiceRepository.findByStatusAndActualEndTimeAndBranch(Constants.APPOINTMENT_SERVICE_STATUS.COMPLETED, instantStartTime, instantEndTime, branchId);
        List<AppointmentService> apptServices = new ArrayList<>();
        for (AppointmentMaster am : ams) {
            apptServices.addAll(am.getAppointmentServices().stream().filter(as -> as.getStatus().getCode().equals(Constants.APPOINTMENT_SERVICE_STATUS.COMPLETED)).collect(Collectors.toList()));
        }
        List<RevenueByServiceDTO> result = new ArrayList<>();
        Map<String, List<AppointmentService>> apptServicesByService = apptServices.stream().collect(Collectors.groupingBy(appointmentService -> appointmentService.getSpaService().getName()));
        for (String serviceName : apptServicesByService.keySet()) {
            RevenueByServiceDTO revenueByServiceDTO = new RevenueByServiceDTO();
            revenueByServiceDTO.setServiceName(serviceName);
            Integer totalCompletedApptService = 0;
            Double total = 0D;
            Double totalPayAmount = 0D;
            for (AppointmentService appointmentService : apptServicesByService.get(serviceName)) {
                totalCompletedApptService += 1;
                total += Objects.nonNull(appointmentService.getTotal()) ? appointmentService.getTotal() : 0D;
                totalPayAmount += Objects.nonNull(appointmentService.getPayAmount()) ? appointmentService.getPayAmount() : 0D;
            }
            revenueByServiceDTO.setTotalCompletedApptService(totalCompletedApptService);
            revenueByServiceDTO.setTotal(total);
            revenueByServiceDTO.setTotalPayAmount(totalPayAmount);
            result.add(revenueByServiceDTO);
        }
        responseDTO.setData(result);
        return responseDTO;
    }

    @Override
    public ResponseDTO getProductivityInDay(Long branchId) {
        ResponseDTO responseDTO = ResponseUtils.responseOK(null);
        Instant now = Instant.now();
        Instant startTime = DateUtils.atStartOfDay(now, systemTimezone);
        Instant endTime = DateUtils.atEndOfDay(now, systemTimezone);

        List<AppointmentMaster> readyAppts = appointmentMasterRepository.findByExpectedStartTimeBetweenAndStatusCodeAndBranchId(startTime, endTime, Constants.APPOINTMENT_MASTER_STATUS.READY, branchId);
        List<AppointmentMaster> inprocessAppts = appointmentMasterRepository.findByActualStartTimeBetweenAndStatusCodeAndBranchId(startTime, endTime, Constants.APPOINTMENT_MASTER_STATUS.IN_PROGRESS, branchId);
        List<AppointmentMaster> closedAppts = appointmentMasterRepository.findByActualEndTimeBetweenAndStatusCodeAndBranchId(startTime, endTime, Constants.APPOINTMENT_MASTER_STATUS.CLOSED, branchId);

        List<ProductivityPerHourDTO> productivityPerHours = calculateProductivityPerHour(readyAppts, inprocessAppts, closedAppts);
        responseDTO.setData(productivityPerHours);
        return responseDTO;
    }

    @Override
    public ByteArrayInputStream downloadProductivityInDay(List<Long> listBranchId) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        List<Branch> branches = new ArrayList<>();
        Instant now = Instant.now();
        Instant startTime = DateUtils.atStartOfDay(now, systemTimezone);
        Instant endTime = DateUtils.atEndOfDay(now, systemTimezone);

        if (CollectionUtils.isEmpty(listBranchId)) {
            branches = branchRepository.findAll();
        } else {
            branches = branchRepository.findAllById(listBranchId);
        }

        for (Branch branch : branches) {
            createProductivitySheetForBranch(workbook, branch, startTime, endTime);
        }

        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            workbook.write(result);
            return new ByteArrayInputStream(result.toByteArray());
        } catch (Exception e) {
            throw new SSDSBusinessException(null, "Có lỗi xảy ra khi tải dữ liệu " + e);
        }

    }

    @Override
    public ByteArrayInputStream downloadRevenueByService(ZonedDateTime startTime, ZonedDateTime endTime, List<Long> listBranchId) {
        Map<String, Instant> instantMap = DateUtils.zonedDateTimeToInstant(startTime, endTime, systemTimezone);
        Instant instantStartTime = instantMap.get("startTime");
        Instant instantEndTime = instantMap.get("endTime");


        List<Branch> branches = new ArrayList<>();
        if (CollectionUtils.isEmpty(listBranchId)) {
            branches = branchRepository.findAll();
        } else {
            branches = branchRepository.findAllById(listBranchId);
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        for (Branch branch : branches) {
            createRevenueByServiceSheetForBranch(workbook, branch, instantStartTime, instantEndTime);
        }

        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            workbook.write(result);
            return new ByteArrayInputStream(result.toByteArray());
        } catch (Exception e) {
            throw new SSDSBusinessException(null, "Có lỗi xảy ra khi tải dữ liệu " + e);
        }
    }

    @Override
    public ApptPerformanceDTO getAppointmentPerformance(Long startTime, Long endTime, String period, Long branchId) {
        ApptPerformanceDTO apptPerformance = new ApptPerformanceDTO();
        Optional<Branch> branchOpt = branchRepository.findById(branchId);
        if (branchOpt.isPresent()) {
            Branch branch = branchOpt.get();
            apptPerformance.setBranchName(branch.getName());
            apptPerformance.setBranchId(branch.getId());
            apptPerformance.setBranchCode(branch.getCode());

            Instant from = Instant.ofEpochMilli(startTime);
            Instant to = Instant.ofEpochMilli(endTime);

            Instant lastFrom = Instant.now();
            Instant lastTo = Instant.now();

            switch (period) {
                case Constants.PERIOD.MONTH:
                    lastFrom = DateUtils.getStartOfLastMonth(from, systemTimezone);
                    lastTo = DateUtils.getLastOfLastMonth(from, systemTimezone);
                    break;
                case Constants.PERIOD.WEEK:
                    lastFrom = from.minus(7, ChronoUnit.DAYS);
                    lastTo = to.minus(7, ChronoUnit.DAYS);
                    break;
                case Constants.PERIOD.DAY:
                    lastFrom = from.minus(1, ChronoUnit.DAYS);
                    lastTo = to.minus(1, ChronoUnit.DAYS);
                    break;
            }

            if (!period.equals(Constants.PERIOD.DAY)) {
                List<PerformanceMetric> currentPmList = performanceMetricRepository.findByTimeBetweenAndBranchId(from, to, branchId);
                List<PerformanceMetric> lastPmList = performanceMetricRepository.findByTimeBetweenAndBranchId(lastFrom, lastTo, branchId);
                ApptPerformanceByBranchDTO performanceByBranchDTO = buildApptPerformanceByBranchDTO(currentPmList, lastPmList, period, from, to);

                apptPerformance.setPerformanceByBranch(performanceByBranchDTO);
                return apptPerformance;
            } else {
                List<MetricPerHour> curMetricPerHours = appointmentMasterRepository.calculateMetricInDay(branchId, from, to);
                List<MetricPerHour> lastMetricPerHours = appointmentMasterRepository.calculateMetricInDay(branchId, lastFrom, lastTo);

                ApptPerformanceByBranchDTO performanceByBranchDTO = buildApptPerformanceByBranchDTOForDay(curMetricPerHours, lastMetricPerHours, from, to);
                apptPerformance.setPerformanceByBranch(performanceByBranchDTO);
                return apptPerformance;
            }
        }
        throw new SSDSBusinessException(ErrorConstants.BRANCH_NOT_EXIST);
    }

    private ApptPerformanceByBranchDTO buildApptPerformanceByBranchDTO(List<PerformanceMetric> currentPmList, List<PerformanceMetric> lastPmList, String period, Instant from, Instant to) {
        switch (period) {
            case Constants.PERIOD.MONTH:
                return buildApptPerformanceByBranchDTOForDays(currentPmList, lastPmList, from, to, period);
            case Constants.PERIOD.WEEK:
                return buildApptPerformanceByBranchDTOForDays(currentPmList, lastPmList, from, to, period);
        }
        return null;
    }

    private ApptPerformanceByBranchDTO buildApptPerformanceByBranchDTOForDay(List<MetricPerHour> curMetricPerHours, List<MetricPerHour> lastMetricPerHours, Instant from, Instant to) {
        ApptPerformanceByBranchDTO performance = new ApptPerformanceByBranchDTO();

        ApptPerformanceMetrixDTO placeGmv = new ApptPerformanceMetrixDTO();
        performance.setPlaceGmv(placeGmv);
        ApptPerformanceMetrixDTO placeAppointment = new ApptPerformanceMetrixDTO();
        performance.setPlaceAppointment(placeAppointment);
        ApptPerformanceMetrixDTO doneGmv = new ApptPerformanceMetrixDTO();
        performance.setDoneGmv(doneGmv);
        ApptPerformanceMetrixDTO doneAppointment = new ApptPerformanceMetrixDTO();
        performance.setDoneAppointment(doneAppointment);
        ApptPerformanceMetrixDTO cancelledSales = new ApptPerformanceMetrixDTO();
        performance.setCancelledSales(cancelledSales);
        ApptPerformanceMetrixDTO cancelledAppointment = new ApptPerformanceMetrixDTO();
        performance.setCancelledAppointment(cancelledAppointment);
        ApptPerformanceMetrixDTO placeSalesPerAppointment = new ApptPerformanceMetrixDTO();
        performance.setPlaceSalesPerAppointment(placeSalesPerAppointment);

        setPerformanceMetric(curMetricPerHours, lastMetricPerHours, performance);

        List<PointDTO> placeGmvPoints = new ArrayList<>();
        List<PointDTO> placeAppointmentPoints = new ArrayList<>();
        List<PointDTO> doneGmvPoints = new ArrayList<>();
        List<PointDTO> doneAppointmentPoints = new ArrayList<>();
        List<PointDTO> cancelledSalesPoints = new ArrayList<>();
        List<PointDTO> cancelledAppointmentPoints = new ArrayList<>();
        List<PointDTO> placeSalesPerAppointmentPoints = new ArrayList<>();

        Map<Integer, List<MetricPerHour>> curMetricByTime = curMetricPerHours.stream().collect(Collectors.groupingBy(MetricPerHour::getTime));

        Integer hour = 0;
        Instant time = from;
        while (from.plus(hour, ChronoUnit.HOURS).isBefore(to)) {
            time = from.plus(hour, ChronoUnit.HOURS);
            Double placeGmvValue = 0D;
            Double placeApptValue = 0D;
            Double doneGmvValue = 0D;
            Double doneApptValue = 0D;
            Double cancelledSaleValue = 0D;
            Double cancelledApptValue = 0D;
            List<MetricPerHour> metricPerHours = curMetricByTime.get(hour);
            if (Objects.nonNull(metricPerHours)) {
                Map<String, List<MetricPerHour>> metricByType = metricPerHours.stream().collect(Collectors.groupingBy(MetricPerHour::getType));
                List<MetricPerHour> gmvMetrics = metricByType.get("GMV");
                if (CollectionUtils.isNotEmpty(gmvMetrics)) {
                    MetricPerHour gmvMetric = gmvMetrics.get(0);
                    placeGmvValue = Double.valueOf(gmvMetric.getTotal());
                    placeApptValue = Double.valueOf(gmvMetric.getNumOfAm());
                }

                List<MetricPerHour> doneMetrics = metricByType.get("DONE");
                if (CollectionUtils.isNotEmpty(doneMetrics)) {
                    MetricPerHour doneMetric = doneMetrics.get(0);
                    doneGmvValue = Double.valueOf(doneMetric.getTotal());
                    doneApptValue = Double.valueOf(doneMetric.getNumOfAm());
                }

                List<MetricPerHour> canceledMetrics = metricByType.get("CANCELED");
                if (CollectionUtils.isNotEmpty(canceledMetrics)) {
                    MetricPerHour canceledMetric = canceledMetrics.get(0);
                    cancelledSaleValue = Double.valueOf(canceledMetric.getTotal());
                    cancelledApptValue = Double.valueOf(canceledMetric.getNumOfAm());
                }
            }
            PointDTO placeGvmPoint = new PointDTO();
            placeGvmPoint.setTimestamp(time);
            placeGvmPoint.setValue(placeGmvValue);
            placeGmvPoints.add(placeGvmPoint);

            PointDTO placeAppointmentPoint = new PointDTO();
            placeAppointmentPoint.setTimestamp(time);
            placeAppointmentPoint.setValue(placeApptValue);
            placeAppointmentPoints.add(placeAppointmentPoint);

            PointDTO doneGmvPoint = new PointDTO();
            doneGmvPoint.setTimestamp(time);
            doneGmvPoint.setValue(doneGmvValue);
            doneGmvPoints.add(doneGmvPoint);

            PointDTO doneAppointmentPoint = new PointDTO();
            doneAppointmentPoint.setTimestamp(time);
            doneAppointmentPoint.setValue(doneApptValue);
            doneAppointmentPoints.add(doneAppointmentPoint);

            PointDTO cancelledSalesPoint = new PointDTO();
            cancelledSalesPoint.setTimestamp(time);
            cancelledSalesPoint.setValue(cancelledSaleValue);
            cancelledSalesPoints.add(cancelledSalesPoint);

            PointDTO cancelledAppointmentPoint = new PointDTO();
            cancelledAppointmentPoint.setTimestamp(time);
            cancelledAppointmentPoint.setValue(cancelledApptValue);
            cancelledAppointmentPoints.add(cancelledAppointmentPoint);

            PointDTO placeSalesPerAppointmentPoint = new PointDTO();
            placeSalesPerAppointmentPoint.setTimestamp(time);

            if (placeApptValue == 0) {
                placeSalesPerAppointmentPoint.setValue(Double.valueOf(0));
            } else {
                placeSalesPerAppointmentPoint.setValue((double) (Math.round(placeGmvValue / Double.valueOf(placeApptValue) * 10) / 10));
            }
            placeSalesPerAppointmentPoints.add(placeSalesPerAppointmentPoint);
            hour++;
        }
        performance.getPlaceGmv().setPoints(placeGmvPoints);
        performance.getPlaceAppointment().setPoints(placeAppointmentPoints);
        performance.getDoneGmv().setPoints(doneGmvPoints);
        performance.getDoneAppointment().setPoints(doneAppointmentPoints);
        performance.getCancelledSales().setPoints(cancelledSalesPoints);
        performance.getCancelledAppointment().setPoints(cancelledAppointmentPoints);
        performance.getPlaceSalesPerAppointment().setPoints(placeSalesPerAppointmentPoints);
        return performance;
    }

    private void setPerformanceMetric(List<MetricPerHour> curMetricPerHours, List<MetricPerHour> lastMetricPerHours, ApptPerformanceByBranchDTO performance) {
        Double placeGmv = 0D;
        Double placeAppt = 0D;
        Double doneGmv = 0D;
        Double doneAppointment = 0D;
        Double cancelledSales = 0D;
        Double cancelledAppointment = 0D;
        Double placeSalesPerAppointment = 0D;
        for (MetricPerHour metric : curMetricPerHours) {
            if ("GMV".equals(metric.getType())) {
                placeGmv += metric.getTotal();
                placeAppt += metric.getNumOfAm();
            } else if ("DONE".equals(metric.getType())) {
                doneGmv += metric.getTotal();
                doneAppointment += metric.getNumOfAm();
            } else if ("CANCELED".equals(metric.getType())) {
                cancelledSales += metric.getTotal();
                cancelledAppointment += metric.getNumOfAm();
            }
        }
        if (placeAppt != 0) {
            placeSalesPerAppointment = (double) (Math.round(placeGmv / placeAppt * 10) / 10);
        }
        performance.getPlaceGmv().setValue(placeGmv);
        performance.getPlaceAppointment().setValue(placeAppt);
        performance.getDoneGmv().setValue(doneGmv);
        performance.getDoneAppointment().setValue(doneAppointment);
        performance.getCancelledSales().setValue(cancelledSales);
        performance.getCancelledAppointment().setValue(cancelledAppointment);
        performance.getPlaceSalesPerAppointment().setValue(placeSalesPerAppointment);


        Double oldPlaceGmv = 0D;
        Double oldPlaceAppt = 0D;
        Double oldDoneGmv = 0D;
        Double oldDoneAppointment = 0D;
        Double oldCancelledSales = 0D;
        Double oldCancelledAppointment = 0D;
        Double oldPlaceSalesPerAppointment = 0D;
        for (MetricPerHour metric : lastMetricPerHours) {
            if ("GMV".equals(metric.getType())) {
                oldPlaceGmv += metric.getTotal();
                oldPlaceAppt += metric.getNumOfAm();
            } else if ("DONE".equals(metric.getType())) {
                oldDoneGmv += metric.getTotal();
                oldDoneAppointment += metric.getNumOfAm();
            } else if ("CANCELED".equals(metric.getType())) {
                oldCancelledSales += metric.getTotal();
                oldCancelledAppointment += metric.getNumOfAm();
            }
        }
        if (oldPlaceAppt != 0) {
            oldPlaceSalesPerAppointment = (double) (Math.round(oldPlaceGmv / oldPlaceAppt * 10) / 10);
        }
        performance.getPlaceGmv().setOldValue(oldPlaceGmv);
        performance.getPlaceAppointment().setOldValue(oldPlaceAppt);
        performance.getDoneGmv().setOldValue(oldDoneGmv);
        performance.getDoneAppointment().setOldValue(oldDoneAppointment);
        performance.getCancelledSales().setOldValue(oldCancelledSales);
        performance.getCancelledAppointment().setOldValue(oldCancelledAppointment);
        performance.getPlaceSalesPerAppointment().setOldValue(oldPlaceSalesPerAppointment);

        double placeGvmChange = placeGmv - oldPlaceGmv;
        performance.getPlaceGmv().setIncrement(placeGvmChange);
        if (oldPlaceGmv != 0) {
            performance.getPlaceGmv().setChainRatio((placeGvmChange < 0 ? (0 - placeGvmChange) : placeGvmChange) / oldPlaceGmv);
        }

        double placeApptChange = placeAppt - oldPlaceAppt;
        performance.getPlaceAppointment().setIncrement(placeApptChange);
        if (oldPlaceAppt != 0) {
            performance.getPlaceAppointment().setChainRatio((placeApptChange < 0 ? (0 - placeApptChange) : placeApptChange) / oldPlaceAppt);
        }

        double cancelSaleChange = cancelledSales - oldCancelledSales;
        performance.getCancelledSales().setIncrement(cancelSaleChange);
        if (oldCancelledSales != 0) {
            performance.getCancelledSales().setChainRatio((cancelSaleChange < 0 ? (0 - cancelSaleChange) : cancelSaleChange) / oldCancelledSales);
        }

        double cancelledApptChange = cancelledAppointment - oldCancelledAppointment;
        performance.getCancelledAppointment().setIncrement(cancelledApptChange);
        if (oldCancelledAppointment != 0) {
            performance.getCancelledAppointment().setChainRatio((cancelledApptChange < 0 ? (0 - cancelledApptChange) : cancelledApptChange) / oldCancelledAppointment);
        }

        double doneGvmChange = doneGmv - oldDoneGmv;
        performance.getDoneGmv().setIncrement(doneGvmChange);
        if (oldDoneGmv != 0) {
            performance.getDoneGmv().setChainRatio((doneGvmChange < 0 ? (0 - doneGvmChange) : doneGvmChange) / oldDoneGmv);
        }

        double doneApptChange = doneAppointment - oldDoneAppointment;
        performance.getDoneAppointment().setIncrement(doneApptChange);
        if (oldDoneAppointment != 0) {
            performance.getDoneAppointment().setChainRatio((doneApptChange < 0 ? (0 - doneApptChange) : doneApptChange) / oldDoneAppointment);
        }

        if (placeAppt != 0) {
            performance.getPlaceSalesPerAppointment().setValue((double) (Math.round(placeGmv / placeAppt * 10) / 10));
        } else {
            performance.getPlaceSalesPerAppointment().setValue(0d);
        }
        if (oldPlaceAppt != 0) {
            performance.getPlaceSalesPerAppointment().setOldValue((double) (Math.round(oldPlaceGmv / oldPlaceAppt * 10) / 10));
        } else {
            performance.getPlaceSalesPerAppointment().setOldValue(0d);
        }
        double placeSalePerApptChange = performance.getPlaceSalesPerAppointment().getValue() - performance.getPlaceSalesPerAppointment().getOldValue();
        performance.getPlaceSalesPerAppointment().setIncrement(placeSalePerApptChange);
        if (!performance.getPlaceSalesPerAppointment().getOldValue().equals(Double.valueOf(0))) {
            performance.getPlaceSalesPerAppointment().setChainRatio((placeSalePerApptChange < 0 ? (0 - placeSalePerApptChange) : placeSalePerApptChange) / performance.getPlaceSalesPerAppointment().getOldValue());
        }
    }

    private ApptPerformanceByBranchDTO buildApptPerformanceByBranchDTOForDays(List<PerformanceMetric> currentPmList, List<PerformanceMetric> lastPmList, Instant from, Instant to, String period) {
        Long branchId = currentPmList.get(0).getBranch().getId();
        PerformanceMetricDTO currentPm = performanceMetricRepository.sumMetricByTimeAndBranchId(from, to, branchId);
        setDefaultValueForPm(currentPm, branchId);

        PerformanceMetricDTO lastPm = new PerformanceMetricDTO();
        if (Constants.PERIOD.MONTH.equals(period)) {
            lastPm = performanceMetricRepository.sumMetricByTimeAndBranchId(DateUtils.getStartOfLastMonth(from, systemTimezone), DateUtils.getLastOfLastMonth(from, systemTimezone), branchId);
        } else if (Constants.PERIOD.WEEK.equals(period)) {
            lastPm = performanceMetricRepository.sumMetricByTimeAndBranchId(
                from.minus(7, ChronoUnit.DAYS),
                to.minus(7, ChronoUnit.DAYS),
                branchId);
        } else if (Constants.PERIOD.DAY.equals(period)) {
            lastPm = performanceMetricRepository.sumMetricByTimeAndBranchId(
                from.minus(1, ChronoUnit.DAYS),
                to.minus(1, ChronoUnit.DAYS),
                branchId);

        }
        setDefaultValueForPm(lastPm, branchId);
        ApptPerformanceByBranchDTO performance = new ApptPerformanceByBranchDTO();

        ApptPerformanceMetrixDTO placeGmv = new ApptPerformanceMetrixDTO();
        performance.setPlaceGmv(placeGmv);
        ApptPerformanceMetrixDTO placeAppointment = new ApptPerformanceMetrixDTO();
        performance.setPlaceAppointment(placeAppointment);
        ApptPerformanceMetrixDTO doneGmv = new ApptPerformanceMetrixDTO();
        performance.setDoneGmv(doneGmv);
        ApptPerformanceMetrixDTO doneAppointment = new ApptPerformanceMetrixDTO();
        performance.setDoneAppointment(doneAppointment);
        ApptPerformanceMetrixDTO cancelledSales = new ApptPerformanceMetrixDTO();
        performance.setCancelledSales(cancelledSales);
        ApptPerformanceMetrixDTO cancelledAppointment = new ApptPerformanceMetrixDTO();
        performance.setCancelledAppointment(cancelledAppointment);
        ApptPerformanceMetrixDTO placeSalesPerAppointment = new ApptPerformanceMetrixDTO();
        performance.setPlaceSalesPerAppointment(placeSalesPerAppointment);

        setCommonPerformace(performance, currentPm, lastPm);

        List<PointDTO> placeGmvPoints = new ArrayList<>();
        List<PointDTO> placeAppointmentPoints = new ArrayList<>();
        List<PointDTO> doneGmvPoints = new ArrayList<>();
        List<PointDTO> doneAppointmentPoints = new ArrayList<>();
        List<PointDTO> cancelledSalesPoints = new ArrayList<>();
        List<PointDTO> cancelledAppointmentPoints = new ArrayList<>();
        List<PointDTO> placeSalesPerAppointmentPoints = new ArrayList<>();

        for (PerformanceMetric performanceMetric : currentPmList) {
            PointDTO placeGvmPoint = new PointDTO();
            placeGvmPoint.setTimestamp(performanceMetric.getTime());
            placeGvmPoint.setValue(performanceMetric.getPlaceGmv());
            placeGmvPoints.add(placeGvmPoint);

            PointDTO placeAppointmentPoint = new PointDTO();
            placeAppointmentPoint.setTimestamp(performanceMetric.getTime());
            placeAppointmentPoint.setValue(Double.valueOf(performanceMetric.getPlaceAppointment()));
            placeAppointmentPoints.add(placeAppointmentPoint);

            PointDTO doneGmvPoint = new PointDTO();
            doneGmvPoint.setTimestamp(performanceMetric.getTime());
            doneGmvPoint.setValue(performanceMetric.getDoneGmv());
            doneGmvPoints.add(doneGmvPoint);

            PointDTO doneAppointmentPoint = new PointDTO();
            doneAppointmentPoint.setTimestamp(performanceMetric.getTime());
            doneAppointmentPoint.setValue(Double.valueOf(performanceMetric.getDoneAppointment()));
            doneAppointmentPoints.add(doneAppointmentPoint);

            PointDTO cancelledSalesPoint = new PointDTO();
            cancelledSalesPoint.setTimestamp(performanceMetric.getTime());
            cancelledSalesPoint.setValue(performanceMetric.getCancelledSales());
            cancelledSalesPoints.add(cancelledSalesPoint);

            PointDTO cancelledAppointmentPoint = new PointDTO();
            cancelledAppointmentPoint.setTimestamp(performanceMetric.getTime());
            cancelledAppointmentPoint.setValue(Double.valueOf(performanceMetric.getCancelledAppointment()));
            cancelledAppointmentPoints.add(cancelledAppointmentPoint);

            PointDTO placeSalesPerAppointmentPoint = new PointDTO();
            placeSalesPerAppointmentPoint.setTimestamp(performanceMetric.getTime());

            if (performanceMetric.getPlaceAppointment() == 0) {
                placeSalesPerAppointmentPoint.setValue(Double.valueOf(0));
            } else {
                placeSalesPerAppointmentPoint.setValue((double) (Math.round(performanceMetric.getPlaceGmv() / Double.valueOf(performanceMetric.getPlaceAppointment()) * 10) / 10));
            }
            placeSalesPerAppointmentPoints.add(placeSalesPerAppointmentPoint);
        }

        performance.getPlaceGmv().setPoints(placeGmvPoints);
        performance.getPlaceAppointment().setPoints(placeAppointmentPoints);
        performance.getDoneGmv().setPoints(doneGmvPoints);
        performance.getDoneAppointment().setPoints(doneAppointmentPoints);
        performance.getCancelledSales().setPoints(cancelledSalesPoints);
        performance.getCancelledAppointment().setPoints(cancelledAppointmentPoints);
        performance.getPlaceSalesPerAppointment().setPoints(placeSalesPerAppointmentPoints);

        return performance;
    }

    private void setDefaultValueForPm(PerformanceMetricDTO pm, Long branchId) {
        if (Objects.isNull(pm.getBranchId())) {
            pm.setBranchId(branchId);
        }
        if (Objects.isNull(pm.getPlaceGmv())) {
            pm.setPlaceGmv(0D);
        }
        if (Objects.isNull(pm.getPlaceAppointment())) {
            pm.setPlaceAppointment(0L);
        }
        if (Objects.isNull(pm.getDoneGmv())) {
            pm.setDoneGmv(0D);
        }
        if (Objects.isNull(pm.getDoneAppointment())) {
            pm.setDoneAppointment(0L);
        }
        if (Objects.isNull(pm.getCancelledAppointment())) {
            pm.setCancelledAppointment(0L);
        }
        if (Objects.isNull(pm.getCancelledSales())) {
            pm.setCancelledSales(0D);
        }
    }

    private void setCommonPerformace(ApptPerformanceByBranchDTO performance, PerformanceMetricDTO currentPm, PerformanceMetricDTO lastPm) {
        performance.getPlaceGmv().setValue(currentPm.getPlaceGmv());
        performance.getPlaceGmv().setOldValue(lastPm.getPlaceGmv());
        double placeGvmIn = currentPm.getPlaceGmv() - lastPm.getPlaceGmv();
        performance.getPlaceGmv().setIncrement(placeGvmIn);
        if (lastPm.getPlaceGmv() != 0) {
            performance.getPlaceGmv().setChainRatio((placeGvmIn < 0 ? (0 - placeGvmIn) : placeGvmIn) / lastPm.getPlaceGmv());
        }

        performance.getPlaceAppointment().setValue(Double.valueOf(currentPm.getPlaceAppointment()));
        performance.getPlaceAppointment().setOldValue(Double.valueOf(lastPm.getPlaceAppointment()));
        double placeApptChange = currentPm.getPlaceAppointment() - lastPm.getPlaceAppointment();
        performance.getPlaceAppointment().setIncrement(placeApptChange);
        if (lastPm.getPlaceAppointment() != 0) {
            performance.getPlaceAppointment().setChainRatio((placeApptChange < 0 ? (0 - placeApptChange) : placeApptChange) / lastPm.getPlaceAppointment());
        }

        performance.getCancelledSales().setValue(Double.valueOf(currentPm.getCancelledSales()));
        performance.getCancelledSales().setOldValue(Double.valueOf(lastPm.getCancelledSales()));
        double cancelSaleChange = currentPm.getCancelledSales() - lastPm.getCancelledSales();
        performance.getCancelledSales().setIncrement(cancelSaleChange);
        if (lastPm.getCancelledSales() != 0) {
            performance.getCancelledSales().setChainRatio((cancelSaleChange < 0 ? (0 - cancelSaleChange) : cancelSaleChange) / lastPm.getCancelledSales());
        }

        performance.getCancelledAppointment().setValue(Double.valueOf(currentPm.getCancelledAppointment()));
        performance.getCancelledAppointment().setOldValue(Double.valueOf(lastPm.getCancelledAppointment()));
        double cancelledApptChange = currentPm.getCancelledAppointment() - lastPm.getCancelledAppointment();
        performance.getCancelledAppointment().setIncrement(cancelledApptChange);
        if (lastPm.getCancelledAppointment() != 0) {
            performance.getCancelledAppointment().setChainRatio((cancelledApptChange < 0 ? (0 - cancelledApptChange) : cancelledApptChange) / lastPm.getCancelledAppointment());
        }

        performance.getDoneGmv().setValue(Double.valueOf(currentPm.getDoneGmv()));
        performance.getDoneGmv().setOldValue(Double.valueOf(lastPm.getDoneGmv()));
        double doneGvmChange = currentPm.getDoneGmv() - lastPm.getDoneGmv();
        performance.getDoneGmv().setIncrement(doneGvmChange);
        if (lastPm.getDoneGmv() != 0) {
            performance.getDoneGmv().setChainRatio((doneGvmChange < 0 ? (0 - doneGvmChange) : doneGvmChange) / lastPm.getDoneGmv());
        }

        performance.getDoneAppointment().setValue(Double.valueOf(currentPm.getDoneAppointment()));
        performance.getDoneAppointment().setOldValue(Double.valueOf(lastPm.getDoneAppointment()));
        double doneApptChange = currentPm.getDoneAppointment() - lastPm.getDoneAppointment();
        performance.getDoneAppointment().setIncrement(doneApptChange);
        if (lastPm.getDoneAppointment() != 0) {
            performance.getDoneAppointment().setChainRatio((doneApptChange < 0 ? (0 - doneApptChange) : doneApptChange) / lastPm.getDoneAppointment());
        }

        Double placeAppt = performance.getPlaceAppointment().getValue();
        Double placeGmv = performance.getPlaceGmv().getValue();
        Double oldPlaceAppt = performance.getPlaceAppointment().getOldValue();
        Double oldPlaceGmv = performance.getPlaceGmv().getOldValue();
        if (placeAppt != 0) {
            performance.getPlaceSalesPerAppointment().setValue((double) (Math.round(placeGmv / placeAppt * 10) / 10));
        } else {
            performance.getPlaceSalesPerAppointment().setValue(0d);
        }
        if (oldPlaceAppt != 0) {
            performance.getPlaceSalesPerAppointment().setOldValue((double) (Math.round(oldPlaceGmv / oldPlaceAppt * 10) / 10));
        } else {
            performance.getPlaceSalesPerAppointment().setOldValue(0d);
        }
        double placeSalePerApptChange = performance.getPlaceSalesPerAppointment().getValue() - performance.getPlaceSalesPerAppointment().getOldValue();
        performance.getPlaceSalesPerAppointment().setIncrement(placeSalePerApptChange);
        if (!performance.getPlaceSalesPerAppointment().getOldValue().equals(Double.valueOf(0))) {
            performance.getPlaceSalesPerAppointment().setChainRatio((placeSalePerApptChange < 0 ? (0 - placeSalePerApptChange) : placeSalePerApptChange) / performance.getPlaceSalesPerAppointment().getOldValue());
        }
    }


    private void createRevenueByServiceSheetForBranch(XSSFWorkbook workbook, Branch branch, Instant instantStartTime, Instant instantEndTime) {
        List<AppointmentService> apptServices = appointmentServiceRepository.findByStatusAndActualEndTimeAndBranch(Constants.APPOINTMENT_SERVICE_STATUS.COMPLETED, instantStartTime, instantEndTime, branch.getId());
        List<String> headerName = Arrays.asList(
            "Tên dịch vụ",
            "Mã dịch vụ lịch hẹn",
            "Trạng thái dịch vụ lịch hẹn",
            "Thời gian bắt đầu thực tế",
            "Thời gian kết thúc thực tế",
            "Chuyên viên thực hiện",
            "Thành tiền",
            "Tổng thanh toán",
            "Ghi chú");
        Sheet sheet = excelHelper.writeHeaderLine(workbook, branch.getName(), headerName);

        Integer rowCount = 1;
        CellStyle style = excelHelper.createDataFont(workbook);
        Map<String, List<AppointmentService>> apptServicesBySpaService = apptServices.stream().collect(Collectors.groupingBy(service -> service.getSpaService().getName()));
        for (String spaService : apptServicesBySpaService.keySet()) {
            List<AppointmentService> serviceList = apptServicesBySpaService.get(spaService);
            if (CollectionUtils.isNotEmpty(serviceList)) {
                rowCount = writeRevenueByServiceDataLines(serviceList, spaService, workbook, sheet, rowCount, style);
            }
        }
    }

    private Integer writeRevenueByServiceDataLines(List<AppointmentService> serviceList, String serviceName, XSSFWorkbook workbook, Sheet sheet, Integer rowCount, CellStyle style) {
        Integer totalCompletedApptService = 0;
        Double total = 0D;
        Double totalPayAmount = 0D;

        for (int j = 0; j < serviceList.size(); j++) {
            AppointmentService appointmentService = serviceList.get(j);
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            if (j == 0) {
                excelHelper.createCell(sheet, row, columnCount, serviceName, style);
            }
            columnCount++;
            excelHelper.createCell(sheet, row, columnCount++, appointmentService.getId(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentService.getStatus().getName(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentService.getActualStartTime(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentService.getActualEndTime(), style);
            excelHelper.createCell(sheet, row, columnCount++, "Tên chuyên viên", style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentService.getTotal(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentService.getPayAmount(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentService.getNote(), style);
            totalCompletedApptService += 1;
            totalPayAmount += Objects.nonNull(appointmentService.getPayAmount()) ? appointmentService.getPayAmount() : 0D;
        }

        CellStyle summaryStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        summaryStyle.setFont(font);
        int columnCount = 4;

        Row row = sheet.createRow(rowCount++);
        excelHelper.createCell(sheet, row, columnCount++, "Tống số dịch vụ hoàn thành", summaryStyle);
        excelHelper.createCell(sheet, row, columnCount++, totalCompletedApptService, summaryStyle);
        excelHelper.createCell(sheet, row, columnCount++, "Tổng doanh thu", summaryStyle);
        excelHelper.createCell(sheet, row, columnCount++, totalPayAmount, summaryStyle);
        return rowCount;
    }

    private void createProductivitySheetForBranch(XSSFWorkbook workbook, Branch branch, Instant startTime, Instant endTime) {
        Long branchId = branch.getId();
        List<AppointmentMaster> readyAppts = appointmentMasterRepository.findByExpectedStartTimeBetweenAndStatusCodeAndBranchId(startTime, endTime, Constants.APPOINTMENT_MASTER_STATUS.READY, branchId);
        List<AppointmentMaster> inprocessAppts = appointmentMasterRepository.findByActualStartTimeBetweenAndStatusCodeAndBranchId(startTime, endTime, Constants.APPOINTMENT_MASTER_STATUS.IN_PROGRESS, branchId);
        List<AppointmentMaster> closedAppts = appointmentMasterRepository.findByActualEndTimeBetweenAndStatusCodeAndBranchId(startTime, endTime, Constants.APPOINTMENT_MASTER_STATUS.CLOSED, branchId);

        List<String> headerName = Arrays.asList(
            "Thời gian",
            "Mã lịch hẹn",
            "Trạng thái lịch hẹn",
            "Tên khách hàng",
            "Số điện thoại khách hàng",
            "Thời gian bắt đầu dự kiến",
            "Thời gian kết thúc dự kiến",
            "Thời gian bắt đầu thực tế",
            "Thời gian kết thúc thực tế",
            "Thành tiền",
            "Tổng thanh toán",
            "Ghi chú");
        Sheet sheet = excelHelper.writeHeaderLine(workbook, branch.getName(), headerName);

        Integer rowCount = 1;
        for (Integer i = 0; i < 24; i++) {
            final Integer hour = i;
            List<AppointmentMaster> readyAmList = readyAppts.stream().filter(am -> hour.equals(DateUtils.dateToLocalDateTime(am.getExpectedStartTime(), systemTimezone).getHour())).collect(Collectors.toList());
            List<AppointmentMaster> inprocessAmList = inprocessAppts.stream().filter(am -> hour.equals(DateUtils.dateToLocalDateTime(am.getActualStartTime(), systemTimezone).getHour())).collect(Collectors.toList());
            List<AppointmentMaster> closedAmList = closedAppts.stream().filter(am -> hour.equals(DateUtils.dateToLocalDateTime(am.getActualEndTime(), systemTimezone).getHour())).collect(Collectors.toList());
            List<AppointmentMaster> listAmByHour = new ArrayList<>();
            listAmByHour.addAll(readyAmList);
            listAmByHour.addAll(inprocessAmList);
            listAmByHour.addAll(closedAmList);
            if (CollectionUtils.isNotEmpty(listAmByHour)) {
                Instant time = startTime;
                time = time.plus(hour, ChronoUnit.HOURS);
                rowCount = writeDataLines(listAmByHour, time, workbook, sheet, rowCount);
            }
        }
    }

    private Integer writeDataLines(List<AppointmentMaster> listAmByHour, Instant time, XSSFWorkbook workbook, Sheet sheet, Integer rowCount) {
        CellStyle style = excelHelper.createDataFont(workbook);

        for (int j = 0; j < listAmByHour.size(); j++) {
            AppointmentMaster appointmentMaster = listAmByHour.get(j);
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            if (j == 0) {
                excelHelper.createCell(sheet, row, columnCount, time, style);
            }
            columnCount++;
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getId(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getStatus().getName(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getCustomer().getFullName(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getCustomer().getPhoneNumber(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getExpectedStartTime(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getExpectedEndTime(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getActualStartTime(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getActualEndTime(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getTotal(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getPayAmount(), style);
            excelHelper.createCell(sheet, row, columnCount++, appointmentMaster.getNote(), style);
        }
        return rowCount;
    }

    private List<ProductivityPerHourDTO> calculateProductivityPerHour(List<AppointmentMaster> readyAppts, List<AppointmentMaster> inprocessAppts, List<AppointmentMaster> closedAppts) {
        List<ProductivityPerHourDTO> productivityPerHourDTOS = new ArrayList<>();
        for (Integer i = 0; i < 24; i++) {
            ProductivityPerHourDTO productivityPerHourDTO = new ProductivityPerHourDTO();
            productivityPerHourDTO.setHour(i);
            final Integer hour = i;
            long totalReady = readyAppts.stream().filter(am -> hour.equals(DateUtils.dateToLocalDateTime(am.getExpectedStartTime(), systemTimezone).getHour())).count();
            long totalInprocess = inprocessAppts.stream().filter(am -> hour.equals(DateUtils.dateToLocalDateTime(am.getActualStartTime(), systemTimezone).getHour())).count();
            long totalClosed = closedAppts.stream().filter(am -> hour.equals(DateUtils.dateToLocalDateTime(am.getActualEndTime(), systemTimezone).getHour())).count();
            productivityPerHourDTO.setTotalReady((int) totalReady);
            productivityPerHourDTO.setTotalInprocess((int) totalInprocess);
            productivityPerHourDTO.setTotalClosed((int) totalClosed);
            productivityPerHourDTOS.add(productivityPerHourDTO);
        }
        return productivityPerHourDTOS;
    }
}
