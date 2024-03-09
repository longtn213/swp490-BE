package com.fpt.ssds.job;

import com.fpt.ssds.service.dto.BranchDto;
import com.fpt.ssds.service.AppointmentTrackingService;
import com.fpt.ssds.service.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobInsertAppointmentTracking {
    private final AppointmentTrackingService appointmentTrackingService;

    private final BranchService branchService;

    @Scheduled(cron = "${ssds.scheduler.cronjob.create-record-appointment-tracking}")
    @SchedulerLock(name = "schedulerCreateDataApptTracking", lockAtMostFor = "5s", lockAtLeastFor = "5s")
    public void schedulerCreateDataApptTracking() {
        log.info("============== start schedulerCreateDataApptTracking ==============");
        List<BranchDto> allBranches = branchService.getAll();
        if (CollectionUtils.isNotEmpty(allBranches)) {
            for (BranchDto branchDto : allBranches) {
                appointmentTrackingService.autoCreateData(branchDto.getId());
            }
        }
        log.info("============== done schedulerCreateDataApptTracking ==============");
    }
}
