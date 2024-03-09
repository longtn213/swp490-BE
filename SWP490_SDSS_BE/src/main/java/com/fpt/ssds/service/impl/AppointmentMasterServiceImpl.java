package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.*;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.repository.*;
import com.fpt.ssds.service.*;
import com.fpt.ssds.service.dto.*;
import com.fpt.ssds.service.kafka.KafkaStateChangeService;
import com.fpt.ssds.service.mapper.AppointmentMasterMapper;
import com.fpt.ssds.service.state.AppointmentMasterStateHandler;
import com.fpt.ssds.service.state.AppointmentServiceStateHandler;
import com.fpt.ssds.utils.DateUtils;
import com.fpt.ssds.utils.ResponseUtils;
import com.fpt.ssds.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
public class AppointmentMasterServiceImpl implements AppointmentMasterService {

    @Value("${ssds.config.timezone}")
    String systemTimezone;

    private final AppointmentMasterRepository appointmentMasterRepository;

    private final AppointmentMasterMapper appointmentMasterMapper;

    private final MessageSource messageSource;

    private final BranchRepository branchRepository;

    private final SpaServiceRepository spaServiceRepository;

    private final AppointmentTrackingService appointmentTrackingService;

    private final ConfigDataService configDataService;

    private final LookupService lookupService;

    private final UserService userService;

    private final SessionService sessionService;

    private final PriceRepository priceRepository;

    private final ReasonMessageRepository reasonMessageRepository;

    private final AppointmentServiceStateHandler appointmentServiceStateHandler;

    private final ReasonMappingService reasonMappingService;

    private final AppointmentMasterStateHandler appointmentMasterStateHandler;

    private final KafkaStateChangeService kafkaStateChangeService;

    private final BranchService branchService;

    private final SMSService smsService;

    private final FileService fileService;

    @Override
    @Transactional
    public AppointmentMaster createUpdate(AppointmentMasterDto appointmentMasterDto) {
        if (Objects.isNull(appointmentMasterDto.getId())) {
            return createAppointmentMaster(appointmentMasterDto);
        } else {
            return updateAppointmentMaster(appointmentMasterDto);
        }
    }

    @Override
    @Transactional
    public AppointmentMasterDto getById(Long apptMasterId, User user) {
        AppointmentMaster appointmentMaster = findById(apptMasterId);
        if (Constants.ROLE.CUSTOMER.equals(user.getRole().getCode())
            && !user.getId().equals(appointmentMaster.getCustomer().getId())) {
            throw new SSDSBusinessException(null, messageSource.getMessage("appointment.master.get.detail.user.invalid", null, null));
        }

        AppointmentMasterDto appointmentMasterDto = appointmentMasterMapper.toDto(appointmentMaster);
        List<FileDto> invoices = fileService.findByTypeAndRefIdAndUploadStatus(FileType.INVOICE, apptMasterId, UploadStatus.SUCCESS);
        appointmentMasterDto.setInvoices(invoices);
        List<FileDto> imgBefore = fileService.findByTypeAndRefIdAndUploadStatus(FileType.IMG_BEFORE, apptMasterId, UploadStatus.SUCCESS);
        appointmentMasterDto.setImgBefore(imgBefore);
        List<FileDto> imgAfter = fileService.findByTypeAndRefIdAndUploadStatus(FileType.IMG_AFTER, apptMasterId, UploadStatus.SUCCESS);
        appointmentMasterDto.setImgBefore(imgAfter);
        return appointmentMasterDto;
    }

    @Override
    public AppointmentMaster findById(Long apptMasterId) {
        Optional<AppointmentMaster> apptMasterOpt = appointmentMasterRepository.findById(apptMasterId);
        if (apptMasterOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.APPOINTMENT_MASTER_NOT_EXIST);
        }
        return apptMasterOpt.get();
    }

    @Override
    @Transactional
    public void confirmAppointments(List<ConfirmAppointmentRequestDto> confirmAppointmentRequestDtos) {
        List<AppointmentMaster> appointmentMasters = validateAppointmentStatusForConfirm(confirmAppointmentRequestDtos);
        Map<Long, ReasonMessage> reasonMessageMap = this.validateConfirmReason(confirmAppointmentRequestDtos);

        for (AppointmentMaster appointmentMaster : appointmentMasters) {
            ConfirmAppointmentRequestDto confirmRequestDto = confirmAppointmentRequestDtos.stream().filter(requestDto -> appointmentMaster.getId().equals(requestDto.getApptMasterId())).findFirst().get();
            if (Constants.ACTION_CONFIRM.CONFIRM.equals(confirmRequestDto.getAction())) {
                appointmentServiceStateHandler.updateListApptServiceState(appointmentMaster.getAppointmentServices().stream().collect(Collectors.toList()), Constants.APPOINTMENT_SERVICE_STATUS.READY);
                appointmentMaster.setNote(confirmRequestDto.getNote());
//                KafkaDataDTO kafkaDataDTO = KafkaUtils.buildKafkaDataDTO(Constants.KAFKA_TYPE.AM_CONFIRMED, appointmentMaster.getBranch().getCode(), appointmentMaster.getId());
//                CompletableFuture.runAsync(() -> kafkaStateChangeService.notifyStateChangeWithKey(kafkaDataDTO, appointmentMaster.getBranch().getCode()));
            } else {
                appointmentServiceStateHandler.updateListApptServiceState(appointmentMaster.getAppointmentServices().stream().collect(Collectors.toList()), Constants.APPOINTMENT_SERVICE_STATUS.CANCELED);
                ReasonMessage reasonMessage = reasonMessageMap.get(confirmRequestDto.getCanceledReason().getId());
                reasonMappingService.createAndSaveReasonMapping(reasonMessage.getId(), appointmentMaster.getId(), Constants.REF_TYPE.APPOINTMENT_MASTER, Constants.ACTION_CONFIRM.CANCEL, confirmRequestDto.getNote());
                appointmentMaster.setCanceledReason(reasonMessage);
                appointmentMaster.setNote(confirmRequestDto.getNote());
            }
        }
        appointmentMasterRepository.saveAll(appointmentMasters);
    }

    @Override
    @Transactional
    public void cancelAppointmentMaster(List<AppointmentMasterDto> appointmentMasterDtos) {
        List<Long> lstApptId = appointmentMasterDtos.stream().map(AppointmentMasterDto::getId).collect(Collectors.toList());
        List<AppointmentMaster> apptMasters = appointmentMasterRepository.findAllById(lstApptId);

        validateApptMasterStatusForCancel(apptMasters);
        Map<Long, ReasonMessage> reasonMessageMap = validateCancelReason(appointmentMasterDtos);

        List<AppointmentService> services = new ArrayList<>();
        for (AppointmentMaster appointmentMaster : apptMasters) {
            AppointmentMasterDto apptDto = appointmentMasterDtos.stream().filter(appointmentMasterDto -> appointmentMaster.getId().equals(appointmentMasterDto.getId())).findFirst().get();
            services.addAll(appointmentMaster.getAppointmentServices());

            ReasonMessage reasonMessage = reasonMessageMap.get(apptDto.getCanceledReason().getId());
            reasonMappingService.createAndSaveReasonMapping(reasonMessage.getId(), appointmentMaster.getId(), Constants.REF_TYPE.APPOINTMENT_MASTER, Constants.ACTION_CONFIRM.CANCEL, apptDto.getNote());
            appointmentMaster.setCanceledReason(reasonMessage);
            appointmentMaster.setNote(apptDto.getNote());
        }

        appointmentServiceStateHandler.updateListApptServiceState(services, Constants.APPOINTMENT_SERVICE_STATUS.CANCELED);
    }

    @Override
    public ResponseDTO checkin(AppointmentMaster appointmentMaster, AppointmentMasterDto appointmentMasterDto) {
        validateCheckinAppointment(appointmentMaster);
        validateRequiredCheckinInfo(appointmentMasterDto);
        updateApptCheckinInfo(appointmentMaster, appointmentMasterDto);
        return ResponseUtils.responseOK(null);
    }

    @Override
    public ResponseDTO checkout(AppointmentMaster appointmentMaster, AppointmentMasterDto appointmentMasterDto) {
        validateCheckoutAppointment(appointmentMaster);
        validateRequiredCheckoutInfo(appointmentMasterDto);
        updateApptCheckoutInfo(appointmentMaster, appointmentMasterDto);
        return ResponseUtils.responseOK(null);
    }

    @Override
    @Transactional
    public void sendConfirmMessage(Long amId, String branchCode) {
        Optional<AppointmentMaster> amOpt = appointmentMasterRepository.findByBranchCodeAndId(branchCode, amId);
        if (amOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.APPOINTMENT_MASTER_NOT_EXIST);
        }

        AppointmentMaster appointmentMaster = amOpt.get();
        String branchDetailedAddress = branchService.getBranchDetailedAddress(appointmentMaster.getBranch().getId());
        String message = messageSource.getMessage("sms.message.booking.success.noti", null, null);
        List<String> servicesName = appointmentMaster.getAppointmentServices().stream().map(service -> service.getSpaService().getName()).collect(Collectors.toList());
        String expectedStartTime = DateUtils.formatInstantToString(appointmentMaster.getExpectedStartTime(), systemTimezone, DateUtils.TIME_DATE_FORMAT);
        message = new MessageFormat(message).format(Arrays.asList(StringUtils.join(servicesName, ", "), appointmentMaster.getBranch().getName(), branchDetailedAddress, expectedStartTime).toArray());
        smsService.sendMessage(message, appointmentMaster.getCustomer().getPhoneNumber());
        appointmentMaster.setConfirmMessageSent(Boolean.TRUE);
    }

    @Override
    @Transactional
    public void setOverdueInfo(Long branchId) {
        Integer lateMinutes = configDataService.getIntegerByKey(branchId, Constants.CONFIG_KEY.AM_INTERVAL_ALLOW_LATE, 15);
        List<AppointmentMaster> listAm = appointmentMasterRepository.findAmNeedEnrichOverdueInfo(Instant.now());
        if (CollectionUtils.isNotEmpty(listAm)) {
            for (AppointmentMaster am : listAm) {
                if (Objects.nonNull(am.getActualStartTime())) {
                    if (!am.getActualStartTime().isAfter(am.getExpectedStartTime().plus(lateMinutes, ChronoUnit.MINUTES))) {
                        am.setOverdueStatus(Constants.OVERDUE_STATUS.ONTIME);
                    } else {
                        am.setOverdueStatus(Constants.OVERDUE_STATUS.OVERDUE);
                    }
                } else {
                    am.setOverdueStatus(Constants.OVERDUE_STATUS.OVERDUE);
                }
            }
        }
    }

    private void updateApptCheckoutInfo(AppointmentMaster appointmentMaster, AppointmentMasterDto appointmentMasterDto) {
        if (Objects.nonNull(appointmentMasterDto.getExpectedStartTime())) {
            appointmentMaster.setExpectedStartTime(appointmentMasterDto.getExpectedStartTime());
        }
        if (Objects.nonNull(appointmentMasterDto.getExpectedEndTime())) {
            appointmentMaster.setExpectedEndTime(appointmentMasterDto.getExpectedEndTime());
        }
        if (Objects.nonNull(appointmentMasterDto.getActualStartTime())) {
            appointmentMaster.setActualStartTime(appointmentMasterDto.getActualStartTime());
        }
        if (Objects.nonNull(appointmentMasterDto.getNote())) {
            appointmentMaster.setNote(appointmentMasterDto.getNote());
        }
        if (Objects.isNull(appointmentMasterDto.getActualEndTime())) {
            appointmentMaster.setActualEndTime(Instant.now());
        }
        if (Objects.isNull(appointmentMasterDto.getTotal())) {
            appointmentMaster.setTotal(appointmentMasterDto.getTotal());
        }
        if (Objects.isNull(appointmentMasterDto.getPayAmount())) {
            appointmentMaster.setPayAmount(appointmentMasterDto.getPayAmount());
        }
        if (StringUtils.isNotEmpty(appointmentMasterDto.getPaymentMethod())) {
            appointmentMaster.setPaymentMethod(appointmentMasterDto.getPaymentMethod());
        }

        if (CollectionUtils.isNotEmpty(appointmentMasterDto.getInvoices())) {
            fileService.updateFileRefId(appointmentMasterDto.getInvoices(), appointmentMaster.getId());
        }

        updateApptServiceCheckoutInfo(appointmentMaster.getAppointmentServices(), appointmentMasterDto.getAppointmentServices());

        appointmentMasterStateHandler.updateApptMasterState(appointmentMaster, Constants.APPOINTMENT_MASTER_STATUS.CLOSED);
    }

    private void updateApptServiceCheckoutInfo(List<AppointmentService> services, List<AppointmentServiceDto> servicesDto) {
        Map<Long, AppointmentService> serviceDtoMap = new HashMap<>();
        for (AppointmentService service : services) {
            serviceDtoMap.put(service.getId(), service);
        }

        for (AppointmentServiceDto serviceDto : servicesDto) {
            AppointmentService service = serviceDtoMap.get(serviceDto.getId());
            if (Objects.nonNull(serviceDto)) {
                if (Objects.nonNull(serviceDto.getExpectedStartTime())) {
                    service.setExpectedStartTime(serviceDto.getExpectedStartTime());
                }
                if (Objects.nonNull(serviceDto.getExpectedEndTime())) {
                    service.setExpectedEndTime(serviceDto.getExpectedEndTime());
                }
                if (Objects.nonNull(serviceDto.getActualStartTime())) {
                    if (Objects.isNull(service.getActualStartTime())) {
                        appointmentServiceStateHandler.updateApptServiceState(service, Constants.APPOINTMENT_SERVICE_STATUS.IN_PROGRESS);
                    }
                    service.setActualStartTime(serviceDto.getActualStartTime());
                }
                if (Objects.nonNull(serviceDto.getNote())) {
                    service.setNote(serviceDto.getNote());
                }
                if (Objects.nonNull(serviceDto.getSpecialistInfoNote())) {
                    service.setSpecialistInfoNote(serviceDto.getSpecialistInfoNote());
                }
                if (Objects.nonNull(serviceDto.getSpaServicePrice())) {
                    service.setTotal(serviceDto.getSpaServicePrice());
                }
                if (Objects.nonNull(serviceDto.getPayAmount())) {
                    service.setPayAmount(serviceDto.getPayAmount());
                }
                if (Objects.nonNull(serviceDto.getSpecialist())) {
                    if (Objects.nonNull(serviceDto.getSpecialist().getId())) {
                        User specialist = userService.findByRoleAndId(serviceDto.getSpecialist().getId(), Constants.ROLE.SPECIALIST);
                        service.setSpecialist(specialist);
                    }
                }
                if (Objects.nonNull(serviceDto.getActualEndTime())) {
                    if (Objects.isNull(service.getActualEndTime())) {
                        appointmentServiceStateHandler.updateApptServiceState(service, Constants.APPOINTMENT_SERVICE_STATUS.COMPLETED);
                    }
                    service.setActualEndTime(serviceDto.getActualStartTime());
                }
            }
        }
    }

    private void validateRequiredCheckoutInfo(AppointmentMasterDto appointmentMasterDto) {
        List<AppointmentServiceDto> serviceDtos = appointmentMasterDto.getAppointmentServices();
        if (CollectionUtils.isNotEmpty(serviceDtos)) {
            List<AppointmentServiceDto> missingInfoServices = serviceDtos.stream().filter(serviceDto -> Objects.isNull(serviceDto.getActualStartTime())
                    || Objects.isNull(serviceDto.getActualEndTime())
                    || (Objects.isNull(serviceDto.getSpecialist()) && Objects.isNull(serviceDto.getSpecialistInfoNote())))
                .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(missingInfoServices)) {
                throw new SSDSBusinessException(null, messageSource.getMessage("appointment.master.checkout.services.missing.required.info", null, null));
            }
        }

        if (CollectionUtils.isEmpty(appointmentMasterDto.getInvoices()) || StringUtils.isEmpty(appointmentMasterDto.getPaymentMethod())) {
            throw new SSDSBusinessException(null, messageSource.getMessage("appointment.master.checkout.services.missing.invoices.info", null, null));
        }
    }

    private void validateCheckoutAppointment(AppointmentMaster appointmentMaster) {
        if (!Arrays.asList(Constants.APPOINTMENT_MASTER_STATUS.IN_PROGRESS, Constants.APPOINTMENT_MASTER_STATUS.COMPLETED).contains(appointmentMaster.getStatus().getCode())) {
            throw new SSDSBusinessException(null, messageSource.getMessage("appointment.master.checkout.invalid.status", null, null));
        }
    }

    private void validateRequiredCheckinInfo(AppointmentMasterDto appointmentMasterDto) {
        if (CollectionUtils.isNotEmpty(appointmentMasterDto.getAppointmentServices())) {
            List<AppointmentServiceDto> servicesDTOHaveActualStartTime = appointmentMasterDto.getAppointmentServices().stream().filter(asDTO -> Objects.nonNull(asDTO.getActualStartTime())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(servicesDTOHaveActualStartTime)) {
                throw new SSDSBusinessException(null, messageSource.getMessage("appointment.master.checkin.required.at.least.one.service.has.actual.start.time", null, null));
            }

            List<Long> missingSpecialistInfoServicesId = new ArrayList<>();
            for (AppointmentServiceDto serviceDto : servicesDTOHaveActualStartTime) {
                if (Objects.isNull(serviceDto.getSpecialist()) && Objects.isNull(serviceDto.getSpecialistInfoNote())) {
                    missingSpecialistInfoServicesId.add(serviceDto.getId());
                }
            }

            if (CollectionUtils.isNotEmpty(missingSpecialistInfoServicesId)) {
                String message = messageSource.getMessage("appointment.master.checkin.services.missing.specialist.info", Arrays.asList(StringUtils.join(missingSpecialistInfoServicesId, ", ")).toArray(), null);
                throw new SSDSBusinessException(null, message);
            }
        }
    }

    private void validateCheckinAppointment(AppointmentMaster appointmentMaster) {
        if (!Arrays.asList(Constants.APPOINTMENT_MASTER_STATUS.WAITING_FOR_CONFIRMATION, Constants.APPOINTMENT_MASTER_STATUS.READY).contains(appointmentMaster.getStatus().getCode())) {
            throw new SSDSBusinessException(null, messageSource.getMessage("appointment.master.checkin.invalid.status", null, null));
        }
    }


    private AppointmentMaster createAppointmentMaster(AppointmentMasterDto appointmentMasterDto) {
        validateCommon(appointmentMasterDto);
        User customer = validateCustomer(appointmentMasterDto);

        List<Long> listServiceId = appointmentMasterDto.getAppointmentServices().stream().map(AppointmentServiceDto::getSpaServiceId).collect(Collectors.toList());
        List<SpaService> spaServices = spaServiceRepository.findByIdIn(listServiceId);

        AppointmentMaster appointmentMaster = populateAppointmentMaster(appointmentMasterDto, spaServices, customer);
        appointmentMaster = appointmentMasterRepository.save(appointmentMaster);
        appointmentTrackingService.plusBookedQuantity(appointmentMaster);

        return appointmentMaster;
    }

    private AppointmentMaster populateAppointmentMaster(AppointmentMasterDto appointmentMasterDto, List<SpaService> spaServices, User customer) {
        Boolean confirmationRequired = configDataService.getBooleanByKey(appointmentMasterDto.getBranchId(), Constants.CONFIG_KEY.APPOINTMENT_MASTER_REQUIRE_CONFIRMATION, true);
        Lookup apptMasterStatus = null;
        if (confirmationRequired && appointmentMasterDto.getRequireConfirm()) {
            apptMasterStatus = lookupService.findByKeyAndCode(Constants.LOOKUP_KEY.APPOINTMENT_MASTER_STATUS, Constants.APPOINTMENT_MASTER_STATUS.WAITING_FOR_CONFIRMATION);
        } else {
            apptMasterStatus = lookupService.findByKeyAndCode(Constants.LOOKUP_KEY.APPOINTMENT_MASTER_STATUS, Constants.APPOINTMENT_MASTER_STATUS.READY);
        }

        Map<Long, SpaService> serviceById = new HashMap<>();
        spaServices.forEach(spaService -> serviceById.put(spaService.getId(), spaService));

        AppointmentMaster appointmentMaster = new AppointmentMaster();
        appointmentMaster.setExpectedStartTime(appointmentMasterDto.getExpectedStartTime());
        appointmentMaster.setExpectedEndTime(appointmentMasterDto.getExpectedEndTime());
        appointmentMaster.setTotal(appointmentMasterDto.getTotal());
        appointmentMaster.setPayAmount(appointmentMasterDto.getPayAmount());
        appointmentMaster.setBranch(branchRepository.findById(appointmentMasterDto.getBranchId()).get());
        populateAppointmentService(appointmentMaster, appointmentMasterDto.getAppointmentServices(), serviceById, confirmationRequired);
        appointmentMaster.setCustomer(customer);
        appointmentMaster.setStatus(apptMasterStatus);
        if (Objects.nonNull(appointmentMasterDto.getSessionId())) {
            appointmentMaster.setSession(sessionService.findById(appointmentMasterDto.getSessionId()));
        }


        return appointmentMaster;
    }

    private void populateAppointmentService(AppointmentMaster appointmentMaster, List<AppointmentServiceDto> appointmentServices, Map<Long, SpaService> spaServiceById, Boolean confirmationRequired) {
        Lookup apptServiceStatus = null;
        if (confirmationRequired) {
            apptServiceStatus = lookupService.findByKeyAndCode(Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS, Constants.APPOINTMENT_MASTER_STATUS.WAITING_FOR_CONFIRMATION);
        } else {
            apptServiceStatus = lookupService.findByKeyAndCode(Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS, Constants.APPOINTMENT_MASTER_STATUS.READY);
        }

        List<AppointmentService> apptServices = new ArrayList<>();
        for (AppointmentServiceDto serviceDto : appointmentServices) {
            AppointmentService appointmentService = new AppointmentService();
            appointmentService.setAppointmentMaster(appointmentMaster);
            appointmentService.setExpectedStartTime(serviceDto.getExpectedStartTime());
            appointmentService.setExpectedEndTime(serviceDto.getExpectedEndTime());
            appointmentService.setTotal(serviceDto.getTotal());
            appointmentService.setSpaService(spaServiceById.get(serviceDto.getSpaServiceId()));
            appointmentService.setStatus(apptServiceStatus);
            appointmentService.setOrder(serviceDto.getOrder());
            apptServices.add(appointmentService);
        }

        appointmentMaster.setAppointmentServices(apptServices);
    }


    /*Validation methods*/
    private void validateTimeFields(AppointmentMasterDto appointmentMasterDto) {
        List<FieldErrorDTO> errorDTOS = new ArrayList<>();

        Instant now = Instant.now();
        if (appointmentMasterDto.getExpectedStartTime().isBefore(now)) {
            errorDTOS.add(new FieldErrorDTO("expectedStartTime", messageSource.getMessage("appointment.master.expected.start.time.must.be.after.current.time", null, null)));
        }

        if (!Utils.validatePhoneNumber(appointmentMasterDto.getCustomer().getPhoneNumber())) {
            errorDTOS.add(new FieldErrorDTO("customer.phoneNumber", messageSource.getMessage("phone.number.invalid", null, null)));
        }

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setBranchCode(appointmentMasterDto.getBranchCode());
        bookingRequestDto.setListServicesId(appointmentMasterDto.getAppointmentServices().stream().map(AppointmentServiceDto::getSpaServiceId).collect(Collectors.toList()));
        List<Instant> listAvailableTime = appointmentTrackingService.getAvailableTimeByBranch(bookingRequestDto);
        if (!listAvailableTime.contains(appointmentMasterDto.getExpectedStartTime())) {
            errorDTOS.add(new FieldErrorDTO("expectedStartTime", messageSource.getMessage("appointment.master.expected.start.time.not.available", null, null)));
        }

//        appointmentTrackingRepository.findAllByTimeBetween(appointmentMasterDto.getExpectedStartTime(), appointmentMasterDto.getExpectedEndTime());

        if (CollectionUtils.isNotEmpty(errorDTOS)) {
            throw new SSDSBusinessException(INPUT_INVALID, null, errorDTOS);
        }
    }

    private User validateCustomer(AppointmentMasterDto appointmentMasterDto) {
        User customer = userService.getCustomer(appointmentMasterDto.getCustomer());
        List<AppointmentMaster> apptMasterByCustomerAndTime = appointmentMasterRepository.findByExpectedStartTimeAndCustomerIdAndStatusCodeNotIn(appointmentMasterDto.getExpectedStartTime(), customer.getId(), Arrays.asList(Constants.APPOINTMENT_MASTER_STATUS.CANCELED));
        if (CollectionUtils.isNotEmpty(apptMasterByCustomerAndTime)) {
            String dateTime = DateUtils.formatLocalDateTimeToString(DateUtils.dateToLocalDateTime(appointmentMasterDto.getExpectedStartTime(), systemTimezone), DateUtils.STR_DEFAULT_DATE_TIME_FORMAT);
            String[] dateTimeParts = StringUtils.split(dateTime, " ");
            throw new SSDSBusinessException(ErrorConstants.APPOINTMENT_MASTER_DUPLICATE, Arrays.asList(dateTimeParts[1], dateTimeParts[0]));
        }
        return customer;
    }

    private void validateCommon(AppointmentMasterDto appointmentMasterDto) {
        if (Objects.isNull(appointmentMasterDto.getCustomer().getPhoneNumber())) {
            throw new SSDSBusinessException(ErrorConstants.PHONE_NUMBER_REQUIRED);
        }
        /*Check all data (branch, spaService) exist*/
        validateDataExist(appointmentMasterDto);

        List<AppointmentServiceDto> apptServices = appointmentMasterDto.getAppointmentServices();
        List<Long> listServiceId = apptServices.stream().map(AppointmentServiceDto::getSpaServiceId).collect(Collectors.toList());
        List<SpaService> spaServices = spaServiceRepository.findByIdIn(listServiceId);
        List<Long> existedServicesId = spaServices.stream().map(SpaService::getId).collect(Collectors.toList());
        listServiceId.removeAll(existedServicesId);
        if (CollectionUtils.isNotEmpty(listServiceId)) {
            throw new SSDSBusinessException(ErrorConstants.SPA_SERVICE_NOT_EXIST, Arrays.asList(StringUtils.join(listServiceId, ", ")));
        }
        List<String> inactiveServices = spaServices.stream().filter(s -> !s.getIsActive()).map(SpaService::getName).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(inactiveServices)) {
            throw new SSDSBusinessException(null, messageSource.getMessage("appointment.master.create.spa.service.inactive", Arrays.asList(StringUtils.join(inactiveServices, ", ")).toArray(), null));
        }

        Long totalDuration = 0L;
        for (int i = 0; i < apptServices.size(); i++) {
            AppointmentServiceDto serviceDto = apptServices.get(i);
            serviceDto.setOrder(i + 1);
            SpaService service = spaServices.stream().filter(spaService -> serviceDto.getSpaServiceId().equals(spaService.getId())).findFirst().get();
            if (i == 0) {
                serviceDto.setExpectedStartTime(appointmentMasterDto.getExpectedStartTime());
            } else {
                serviceDto.setExpectedStartTime(apptServices.get(i - 1).getExpectedEndTime());
            }
            serviceDto.setExpectedEndTime(serviceDto.getExpectedStartTime().plus(service.getDuration(), ChronoUnit.MINUTES));
            totalDuration += service.getDuration();
        }
        appointmentMasterDto.setExpectedEndTime(appointmentMasterDto.getExpectedStartTime().plus(totalDuration, ChronoUnit.MINUTES));

        validatePrice(appointmentMasterDto, existedServicesId);
        validateTimeFields(appointmentMasterDto);
    }

    private void validateDataExist(AppointmentMasterDto appointmentMasterDto) {
        Optional<Branch> branchOpt = branchRepository.findById(appointmentMasterDto.getBranchId());
        if (branchOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.BRANCH_NOT_EXIST);
        }
        Branch branch = branchOpt.get();
        if (!branch.getIsActive()) {
            throw new SSDSBusinessException(null, messageSource.getMessage("branch.is.deactive", Arrays.asList(branch.getName()).toArray(), null));
        }
        appointmentMasterDto.setBranchCode(branch.getCode());
    }

    private void validatePrice(AppointmentMasterDto appointmentMasterDto, List<Long> existedServicesId) {
        Double total = priceRepository.getTotalByListService(existedServicesId);
        if (Objects.nonNull(appointmentMasterDto.getTotal())) {
            if (!total.equals(appointmentMasterDto.getTotal())) {
                throw new SSDSBusinessException(ErrorConstants.PRICE_CHANGED);
            }
        }
        appointmentMasterDto.setTotal(total);
        appointmentMasterDto.setPayAmount(total);
    }

    private Map<Long, ReasonMessage> validateConfirmReason(List<ConfirmAppointmentRequestDto> listConfirmApptRequest) {
        List<FieldErrorDTO> errorDTOS = new ArrayList<>();
        List<Long> listReasonId = listConfirmApptRequest.stream().filter(dto -> Objects.nonNull(dto.getCanceledReason())).map(dto -> dto.getCanceledReason().getId()).collect(Collectors.toList());
        List<ReasonMessage> messageList = reasonMessageRepository.findAllActiveReasonMessageByIdAndReasonMessageTypeCode(listReasonId, Constants.MESSAGE_TYPE.CANCEL);

        Map<Long, ReasonMessage> reasonMessageMap = new HashMap<>();
        messageList.stream().forEach(reasonMessage -> {
            reasonMessageMap.put(reasonMessage.getId(), reasonMessage);
        });

        listConfirmApptRequest.forEach(confirmRequestDto -> {
            if (Constants.ACTION_CONFIRM.CANCEL.equals(confirmRequestDto.getAction()) && Objects.isNull(confirmRequestDto.getCanceledReason())) {
                errorDTOS.add(new FieldErrorDTO(confirmRequestDto.getApptMasterId(), "reasonMessage", messageSource.getMessage("appointment.master.cancel.reason.required", null, null)));
            }

            if (Constants.ACTION_CONFIRM.CANCEL.equals(confirmRequestDto.getAction()) && Objects.nonNull(confirmRequestDto.getCanceledReason())) {
                ReasonMessage reasonMessage = reasonMessageMap.get(confirmRequestDto.getCanceledReason().getId());
                if (Objects.isNull(reasonMessage)) {
                    errorDTOS.add(new FieldErrorDTO(confirmRequestDto.getApptMasterId(), "reasonMessage", messageSource.getMessage("appointment.master.cancel.reason.not.exist", null, null)));
                } else if (reasonMessage.getRequireNote() && StringUtils.isEmpty(confirmRequestDto.getNote())) {
                    errorDTOS.add(new FieldErrorDTO(confirmRequestDto.getApptMasterId(), "reasonNote", messageSource.getMessage("appointment.master.cancel.reason.require.note", null, null)));
                }
            }
        });

        if (CollectionUtils.isNotEmpty(errorDTOS)) {
            throw new SSDSBusinessException(INPUT_INVALID, null, errorDTOS);
        }

        return reasonMessageMap;
    }

    private Map<Long, ReasonMessage> validateCancelReason(List<AppointmentMasterDto> appointmentMasterDtos) {
        List<FieldErrorDTO> errorDTOS = new ArrayList<>();
        List<Long> listReasonId = appointmentMasterDtos.stream().filter(dto -> Objects.nonNull(dto.getCanceledReason())).map(dto -> dto.getCanceledReason().getId()).collect(Collectors.toList());
        List<ReasonMessage> messageList = reasonMessageRepository.findAllActiveReasonMessageByIdAndReasonMessageTypeCode(listReasonId, Constants.MESSAGE_TYPE.CANCEL);

        Map<Long, ReasonMessage> reasonMessageMap = new HashMap<>();
        messageList.stream().forEach(reasonMessage -> {
            reasonMessageMap.put(reasonMessage.getId(), reasonMessage);
        });

        appointmentMasterDtos.forEach(apptMaster -> {
            if (Objects.isNull(apptMaster.getCanceledReason())) {
                errorDTOS.add(new FieldErrorDTO(apptMaster.getId(), "canceledReason", messageSource.getMessage("appointment.master.cancel.reason.required", null, null)));
            } else {
                ReasonMessage reasonMessage = reasonMessageMap.get(apptMaster.getCanceledReason().getId());
                if (Objects.isNull(reasonMessage)) {
                    errorDTOS.add(new FieldErrorDTO(apptMaster.getId(), "canceledReason", messageSource.getMessage("appointment.master.cancel.reason.not.exist", null, null)));
                } else if (reasonMessage.getRequireNote() && StringUtils.isEmpty(apptMaster.getNote())) {
                    errorDTOS.add(new FieldErrorDTO(apptMaster.getId(), "canceledNote", messageSource.getMessage("appointment.master.cancel.reason.require.note", null, null)));
                }
            }
        });

        if (CollectionUtils.isNotEmpty(errorDTOS)) {
            throw new SSDSBusinessException(INPUT_INVALID, null, errorDTOS);
        }

        return reasonMessageMap;
    }

    private List<AppointmentMaster> validateAppointmentStatusForConfirm(List<ConfirmAppointmentRequestDto> confirmAppointmentRequestDtos) {
        List<Long> listAppointmentId = confirmAppointmentRequestDtos.stream().map(ConfirmAppointmentRequestDto::getApptMasterId).collect(Collectors.toList());
        List<AppointmentMaster> appointmentMasters = appointmentMasterRepository.findAllById(listAppointmentId);
        List<Long> invalidStatusApptId = appointmentMasters.stream().filter(appointmentMaster -> !Constants.APPOINTMENT_MASTER_STATUS.WAITING_FOR_CONFIRMATION.equals(appointmentMaster.getStatus().getCode())).map(AppointmentMaster::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(invalidStatusApptId)) {
            throw new SSDSBusinessException(null, "Trạng thái lịch hẹn " + StringUtils.join(invalidStatusApptId, ", ") + " không hợp lệ. Vui lòng kiểm tra và thử lại");
        }
        return appointmentMasters;
    }

    private void validateApptMasterStatusForCancel(List<AppointmentMaster> apptMasters) {
        List<String> notCanceledStatus = Arrays.asList(Constants.APPOINTMENT_MASTER_STATUS.CANCELED, Constants.APPOINTMENT_MASTER_STATUS.CLOSED, Constants.APPOINTMENT_MASTER_STATUS.COMPLETED);
        List<AppointmentMaster> invalidAppointmentMaster = apptMasters.stream().filter(appointmentMaster -> notCanceledStatus.contains(appointmentMaster.getStatus().getCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(invalidAppointmentMaster)) {
            String message = messageSource.getMessage("appointment.master.cancel.invalid.status", null, null);
            message = new MessageFormat(message).format(Arrays.asList(StringUtils.join(invalidAppointmentMaster.stream().map(AppointmentMaster::getId).collect(Collectors.toList()), ", ")).toArray());
            throw new SSDSBusinessException(null, message);
        }
    }

    private void updateApptCheckinInfo(AppointmentMaster appointmentMaster, AppointmentMasterDto appointmentMasterDto) {
        if (Objects.nonNull(appointmentMasterDto.getExpectedStartTime())) {
            appointmentMaster.setExpectedStartTime(appointmentMasterDto.getExpectedStartTime());
        }
        if (Objects.nonNull(appointmentMasterDto.getExpectedEndTime())) {
            appointmentMaster.setExpectedEndTime(appointmentMasterDto.getExpectedEndTime());
        }
        if (Objects.nonNull(appointmentMasterDto.getActualStartTime())) {
            appointmentMaster.setActualStartTime(appointmentMasterDto.getActualStartTime());
        }
        if (Objects.nonNull(appointmentMasterDto.getNote())) {
            appointmentMaster.setNote(appointmentMasterDto.getNote());
        }
        if (Objects.isNull(appointmentMasterDto.getActualStartTime())) {
            AppointmentServiceDto firstService = appointmentMasterDto.getAppointmentServices().stream().filter(service -> Objects.nonNull(service.getActualStartTime())).min(Comparator.comparing(AppointmentServiceDto::getActualStartTime)).get();
            appointmentMaster.setActualStartTime(firstService.getActualStartTime());
        }

        if (!appointmentMaster.getActualStartTime().isBefore(appointmentMaster.getExpectedStartTime())) {
            appointmentMaster.setOverdueStatus(Constants.OVERDUE_STATUS.OVERDUE);
        } else {
            appointmentMaster.setOverdueStatus(Constants.OVERDUE_STATUS.ONTIME);
        }

        updateApptServiceCheckinInfo(appointmentMaster.getAppointmentServices(), appointmentMasterDto.getAppointmentServices());
    }

    private void updateApptServiceCheckinInfo(List<AppointmentService> services, List<AppointmentServiceDto> servicesDto) {
        Map<Long, AppointmentService> serviceDtoMap = new HashMap<>();
        for (AppointmentService service : services) {
            serviceDtoMap.put(service.getId(), service);
        }
        for (AppointmentServiceDto serviceDto : servicesDto) {
            AppointmentService service = serviceDtoMap.get(serviceDto.getId());
            if (Objects.nonNull(serviceDto)) {
                if (Objects.nonNull(serviceDto.getExpectedStartTime())) {
                    service.setExpectedStartTime(serviceDto.getExpectedStartTime());
                }
                if (Objects.nonNull(serviceDto.getExpectedEndTime())) {
                    service.setExpectedEndTime(serviceDto.getExpectedEndTime());
                }
                if (Objects.nonNull(serviceDto.getActualStartTime())) {
                    if (Objects.isNull(service.getActualStartTime())) {
                        appointmentServiceStateHandler.updateApptServiceState(service, Constants.APPOINTMENT_SERVICE_STATUS.IN_PROGRESS);
                    }
                    service.setActualStartTime(serviceDto.getActualStartTime());
                    service.setExpectedEndTime(service.getActualStartTime().plus(service.getSpaService().getDuration(), ChronoUnit.MINUTES));
                }
                if (Objects.nonNull(serviceDto.getNote())) {
                    service.setNote(serviceDto.getNote());
                }
                if (Objects.nonNull(serviceDto.getSpecialistInfoNote())) {
                    service.setSpecialistInfoNote(serviceDto.getSpecialistInfoNote());
                }
                if (Objects.nonNull(serviceDto.getSpecialist())) {
                    if (Objects.nonNull(serviceDto.getSpecialist().getId())) {
                        User specialist = userService.findByRoleAndId(serviceDto.getSpecialist().getId(), Constants.ROLE.SPECIALIST);
                        service.setSpecialist(specialist);
                    }
                }
            }
        }
    }

    private AppointmentMaster updateAppointmentMaster(AppointmentMasterDto appointmentMasterDto) {
        Optional<AppointmentMaster> apptOpt = appointmentMasterRepository.findById(appointmentMasterDto.getId());
        if (apptOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.APPOINTMENT_MASTER_NOT_EXIST);
        }
        AppointmentMaster appointmentMaster = apptOpt.get();

        if (!Arrays.asList(Constants.APPOINTMENT_MASTER_STATUS.WAITING_FOR_CONFIRMATION, Constants.APPOINTMENT_MASTER_STATUS.READY).contains(appointmentMaster.getStatus().getCode())) {
            throw new SSDSBusinessException(null, messageSource.getMessage("appointment.master.update.invalid.status", null, null));
        }

        if (!appointmentMasterDto.getBranchId().equals(appointmentMaster.getBranch().getId())) {
            validateCommon(appointmentMasterDto);
            appointmentMaster.setBranch(branchRepository.findById(appointmentMasterDto.getBranchId()).get());
            appointmentMaster = appointmentMasterRepository.save(appointmentMaster);
            appointmentTrackingService.plusBookedQuantity(appointmentMaster);
            appointmentTrackingService.minusBookedQtyBetweenTime(appointmentMaster.getExpectedStartTime(), appointmentMaster.getExpectedEndTime(), appointmentMaster.getBranch().getId());
        }

        if (CollectionUtils.isNotEmpty(appointmentMasterDto.getAppointmentServices())) {
            List<AppointmentService> appointmentServices = appointmentMaster.getAppointmentServices();
            for (AppointmentServiceDto serviceDto : appointmentMasterDto.getAppointmentServices()) {
                AppointmentService service = appointmentServices.stream().filter(s -> s.getId().equals(serviceDto.getId())).findFirst().get();
                if (Objects.nonNull(service.getActualStartTime())) {
                    service.setActualStartTime(serviceDto.getActualStartTime());
                }
                if (Objects.nonNull(service.getActualEndTime())) {
                    service.setActualEndTime(serviceDto.getActualEndTime());
                }
            }
        }

        updateImgForAm(appointmentMaster, appointmentMasterDto);

        return appointmentMaster;
    }

    private void updateImgForAm(AppointmentMaster appointmentMaster, AppointmentMasterDto appointmentMasterDto) {
        if (CollectionUtils.isNotEmpty(appointmentMasterDto.getImgBefore())) {
            fileService.updateFileRefId(appointmentMasterDto.getImgBefore(), appointmentMaster.getId());
        }
        if (CollectionUtils.isNotEmpty(appointmentMasterDto.getImgAfter())) {
            fileService.updateFileRefId(appointmentMasterDto.getImgAfter(), appointmentMaster.getId());
        }
    }
}
