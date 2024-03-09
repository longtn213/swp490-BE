package com.fpt.ssds.job;

import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.repository.BranchRepository;
import com.fpt.ssds.service.PerformanceMetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobSummaryIndexInDay {
    private final PerformanceMetricService performanceMetricService;

    private final BranchRepository branchRepository;

    @Value("${ssds.config.timezone}")
    String systemTimezone;

    @Scheduled(cron = "${ssds.scheduler.cronjob.summary-metrix-in-day}")
    @SchedulerLock(name = "schedulerSummaryMetricInDay", lockAtMostFor = "5s", lockAtLeastFor = "5s")
    public void schedulerSummaryMetricInDay() {
        log.info("============== start schedulerSummaryMetricInDay ==============");
        List<Branch> branches = branchRepository.findAll();
//        Instant time = Instant.now().minus(60, ChronoUnit.DAYS);
        if (CollectionUtils.isNotEmpty(branches)) {
//            while (time.isBefore(Instant.now())) {
            for (Branch branch : branches) {
//                performanceMetricService.summaryMetricInDay(branch, time);
                performanceMetricService.summaryMetricInDay(branch, Instant.now());
//                }
//                time = time.plus(1, ChronoUnit.DAYS);
            }
        }
        log.info("============== done schedulerSummaryMetricInDay ==============");
    }
}
