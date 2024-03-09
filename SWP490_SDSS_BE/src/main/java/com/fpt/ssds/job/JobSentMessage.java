package com.fpt.ssds.job;

import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.repository.AppointmentMasterRepository;
import com.fpt.ssds.service.AppointmentMasterService;
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
public class JobSentMessage {
    private final AppointmentMasterRepository appointmentMasterRepository;

    private final AppointmentMasterService appointmentMasterService;

    @Scheduled(fixedDelayString = "${ssds.scheduler.cronjob.interval-enrich-overdue-info}")
    @SchedulerLock(name = "schedulerSendMessageConfirm", lockAtMostFor = "5s", lockAtLeastFor = "5s")
    public void schedulerSendMessageConfirm() {
        log.info("============== start schedulerSendMessageConfirm ==============");
        List<AppointmentMaster> appointmentMasters = appointmentMasterRepository.findAmNeedSendMessageConfirm();
        for (AppointmentMaster am : appointmentMasters) {
            appointmentMasterService.sendConfirmMessage(am.getId(), am.getBranch().getCode());
        }
        log.info("============== done schedulerSendMessageConfirm ==============");
    }
}
