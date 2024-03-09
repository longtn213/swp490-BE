package com.fpt.ssds.service.impl;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.domain.PerformanceMetric;
import com.fpt.ssds.repository.AppointmentMasterRepository;
import com.fpt.ssds.repository.PerformanceMetricRepository;
import com.fpt.ssds.service.PerformanceMetricService;
import com.fpt.ssds.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PerformanceMetricServiceImpl implements PerformanceMetricService {
    @Value("${ssds.config.timezone}")
    String systemTimezone;

    private final PerformanceMetricRepository performanceMetricRepository;
    private final AppointmentMasterRepository appointmentMasterRepository;

    @Override
    @Transactional
    public void summaryMetricInDay(Branch branch, Instant time) {
        Instant startOfDay = DateUtils.atStartOfDay(time.minus(1, ChronoUnit.DAYS), systemTimezone);
        Instant endOfDay = DateUtils.atEndOfDay(startOfDay, systemTimezone);

        PerformanceMetric performanceMetric = new PerformanceMetric();
        performanceMetric.setBranch(branch);
        performanceMetric.setTime(startOfDay);

        List<AppointmentMaster> amCreatedInDay = appointmentMasterRepository.findByCreatedDateBetweenAndBranchId(startOfDay, endOfDay, branch.getId());
        performanceMetric.setPlaceAppointment((long) amCreatedInDay.size());
        performanceMetric.setPlaceGmv(amCreatedInDay.stream().filter(am -> Objects.nonNull(am.getPayAmount())).mapToDouble(AppointmentMaster::getPayAmount).sum());

        List<AppointmentMaster> amDoneInDay = appointmentMasterRepository.findByActualEndTimeBetweenAndBranchIdAndStatusCode(startOfDay, endOfDay, branch.getId(), Constants.APPOINTMENT_MASTER_STATUS.CLOSED);
        performanceMetric.setDoneAppointment((long) amDoneInDay.size());
        performanceMetric.setDoneGmv(amDoneInDay.stream().filter(am -> Objects.nonNull(am.getPayAmount())).mapToDouble(AppointmentMaster::getPayAmount).sum());

        List<AppointmentMaster> canceledAmInDay = appointmentMasterRepository.findByCancelTimeBetweenAndBranchId(startOfDay, endOfDay, branch.getId());
        performanceMetric.setCancelledAppointment((long) canceledAmInDay.size());
        performanceMetric.setCancelledSales(canceledAmInDay.stream().filter(am -> Objects.nonNull(am.getPayAmount())).mapToDouble(AppointmentMaster::getPayAmount).sum());

        performanceMetricRepository.save(performanceMetric);
    }
}
