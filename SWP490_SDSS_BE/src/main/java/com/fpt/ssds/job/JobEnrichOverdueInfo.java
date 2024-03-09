package com.fpt.ssds.job;

import com.fpt.ssds.service.AppointmentMasterService;
import com.fpt.ssds.service.BranchService;
import com.fpt.ssds.service.dto.BranchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobEnrichOverdueInfo {
    private final BranchService branchService;

    private final AppointmentMasterService appointmentMasterService;

    @Scheduled(fixedDelayString = "${ssds.scheduler.cronjob.interval-enrich-overdue-info}")
    @SchedulerLock(name = "schedulerEnrichOverdueInfo", lockAtMostFor = "5s", lockAtLeastFor = "5s")
    public void schedulerEnrichOverdueInfo() {
        log.info("============== start schedulerEnrichOverdueInfo ==============");
        List<BranchDto> allBranches = branchService.getAll();
        if (CollectionUtils.isNotEmpty(allBranches)) {
            for (BranchDto branchDto : allBranches) {
                appointmentMasterService.setOverdueInfo(branchDto.getId());
            }
        }
        log.info("============== done schedulerEnrichOverdueInfo ==============");
    }
}
