package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.domain.AppointmentService;
import com.fpt.ssds.domain.ReasonMessage;
import com.fpt.ssds.repository.AppointmentMasterRepository;
import com.fpt.ssds.repository.AppointmentServiceRepository;
import com.fpt.ssds.repository.ReasonMessageRepository;
import com.fpt.ssds.service.AppointmentServiceService;
import com.fpt.ssds.service.AppointmentTrackingService;
import com.fpt.ssds.service.ReasonMappingService;
import com.fpt.ssds.service.dto.AppointmentServiceDto;
import com.fpt.ssds.service.dto.FieldErrorDTO;
import com.fpt.ssds.service.state.AppointmentServiceStateHandler;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.fpt.ssds.constant.ErrorConstants.INPUT_INVALID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceServiceImpl implements AppointmentServiceService {
    private final AppointmentServiceRepository appointmentServiceRepository;

    private final AppointmentServiceStateHandler appointmentServiceStateHandler;

    private final MessageSource messageSource;

    private final ReasonMessageRepository reasonMessageRepository;

    private final ReasonMappingService reasonMappingService;

    private final AppointmentTrackingService appointmentTrackingService;

    private final AppointmentMasterRepository appointmentMasterRepository;

    @Override
    @Transactional
    public void cancelListService(List<AppointmentServiceDto> servicesDto) {
        List<Long> lstServicesId = servicesDto.stream().map(AppointmentServiceDto::getId).collect(Collectors.toList());
        List<AppointmentService> services = appointmentServiceRepository.findAllById(lstServicesId);
        List<AppointmentMaster> apptMasters = appointmentMasterRepository.findAllByServiceId(lstServicesId);
        if (apptMasters.size() > 1) {
            throw new SSDSBusinessException(null, messageSource.getMessage("cancel.services.in.same.master.only", null, null));
        }

        validateApptServiceStatusForCancel(services);
        Map<Long, ReasonMessage> reasonMessageMap = validateCancelReason(servicesDto);

        for (AppointmentService service : services) {
            AppointmentServiceDto serviceDto = servicesDto.stream().filter(appointmentMasterDto -> service.getId().equals(appointmentMasterDto.getId())).findFirst().get();

            ReasonMessage reasonMessage = reasonMessageMap.get(serviceDto.getCanceledReason().getId());
            reasonMappingService.createAndSaveReasonMapping(reasonMessage.getId(), service.getId(), Constants.REF_TYPE.APPOINTMENT_SERVICE, Constants.ACTION_CONFIRM.CANCEL, serviceDto.getNote());
            service.setCanceledReason(reasonMessage);
            service.setNote(serviceDto.getNote());

            appointmentTrackingService.minusBookedQtyBetweenTime(service.getExpectedStartTime(), service.getExpectedEndTime(), apptMasters.get(0).getBranch().getId());
        }

        appointmentServiceStateHandler.updateListApptServiceState(services, Constants.APPOINTMENT_SERVICE_STATUS.CANCELED);
        if (apptMasters.get(0).getStatus().getCode() != Constants.APPOINTMENT_MASTER_STATUS.CANCELED) {
            caculateDuration(apptMasters.get(0), services);
        }
    }

    private void caculateDuration(AppointmentMaster apptMaster, List<AppointmentService> apptServices) {
        List<AppointmentService> notCancelledServices = apptServices.stream().filter(service -> Constants.APPOINTMENT_SERVICE_STATUS.CANCELED.equals(service.getStatus().getCode())).collect(Collectors.toList());

        Long totalDuration = 0L;
        for (int i = 0; i < notCancelledServices.size(); i++) {
            AppointmentService service = notCancelledServices.get(i);
            if (i == 0) {
                service.setExpectedStartTime(apptMaster.getExpectedStartTime());
            } else {
                service.setExpectedStartTime(apptServices.get(i - 1).getExpectedEndTime());
            }
            service.setExpectedEndTime(service.getExpectedStartTime().plus(service.getSpaService().getDuration(), ChronoUnit.MINUTES));
            totalDuration += service.getSpaService().getDuration();
        }
        Instant oldEndTime = apptMaster.getExpectedEndTime();
        Instant newEndTime = apptMaster.getExpectedStartTime().plus(totalDuration, ChronoUnit.MINUTES);
        apptMaster.setExpectedEndTime(newEndTime);

        appointmentTrackingService.minusBookedQtyBetweenTime(newEndTime, oldEndTime, apptMaster.getBranch().getId());
        appointmentMasterRepository.save(apptMaster);
    }

    private Map<Long, ReasonMessage> validateCancelReason(List<AppointmentServiceDto> servicesDto) {
        List<FieldErrorDTO> errorDTOS = new ArrayList<>();
        List<Long> listReasonId = servicesDto.stream().filter(dto -> Objects.nonNull(dto.getCanceledReason())).map(dto -> dto.getCanceledReason().getId()).collect(Collectors.toList());
        List<ReasonMessage> messageList = reasonMessageRepository.findAllActiveReasonMessageByIdAndReasonMessageTypeCode(listReasonId, Constants.MESSAGE_TYPE.CANCEL);

        Map<Long, ReasonMessage> reasonMessageMap = new HashMap<>();
        messageList.stream().forEach(reasonMessage -> {
            reasonMessageMap.put(reasonMessage.getId(), reasonMessage);
        });

        servicesDto.forEach(serviceDto -> {
            if (Objects.isNull(serviceDto.getCanceledReason())) {
                errorDTOS.add(new FieldErrorDTO(serviceDto.getId(), "canceledReason", messageSource.getMessage("cancel.reason.required", null, null)));
            } else {
                ReasonMessage reasonMessage = reasonMessageMap.get(serviceDto.getCanceledReason().getId());
                if (Objects.isNull(reasonMessage)) {
                    errorDTOS.add(new FieldErrorDTO(serviceDto.getId(), "canceledReason", messageSource.getMessage("cancel.reason.not.exist", null, null)));
                } else if (reasonMessage.getRequireNote() && StringUtils.isEmpty(serviceDto.getNote())) {
                    errorDTOS.add(new FieldErrorDTO(serviceDto.getId(), "canceledNote", messageSource.getMessage("appointment.master.cancel.reason.require.note", null, null)));
                }
            }
        });

        if (CollectionUtils.isNotEmpty(errorDTOS)) {
            throw new SSDSBusinessException(INPUT_INVALID, null, errorDTOS);
        }

        return reasonMessageMap;
    }

    private void validateApptServiceStatusForCancel(List<AppointmentService> services) {
        List<String> notCanceledStatus = Arrays.asList(Constants.APPOINTMENT_SERVICE_STATUS.CANCELED, Constants.APPOINTMENT_SERVICE_STATUS.COMPLETED);
        List<AppointmentService> invalidServices = services.stream().filter(service -> notCanceledStatus.contains(service.getStatus().getCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(invalidServices)) {
            String message = messageSource.getMessage("appointment.service.cancel.invalid.status", null, null);
            message = new MessageFormat(message).format(Arrays.asList(StringUtils.join(invalidServices.stream().map(AppointmentService::getId).collect(Collectors.toList()), ", ")).toArray());
            throw new SSDSBusinessException(null, message);
        }
    }
}
