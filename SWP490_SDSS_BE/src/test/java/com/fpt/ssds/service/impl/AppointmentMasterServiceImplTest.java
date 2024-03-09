package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.*;
import com.fpt.ssds.repository.*;
import com.fpt.ssds.service.AppointmentTrackingService;
import com.fpt.ssds.service.UserService;
import com.fpt.ssds.service.dto.*;
import com.fpt.ssds.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentMasterServiceImplTest {
    @InjectMocks
    AppointmentMasterServiceImpl appointmentMasterService;

    @Mock
    AppointmentMasterRepository appointmentMasterRepository;

    @Mock
    LookupRepository lookupRepository;

    @Mock
    AppointmentServiceRepository appointmentServiceRepository;

    @Mock
    ReasonMessageRepository reasonMessageRepository;

    @Mock
    MessageSource messageSource;

    @Mock
    BranchRepository branchRepository;

    @Mock
    SpaServiceRepository spaServiceRepository;

    @Mock
    PriceRepository priceRepository;

    @Mock
    AppointmentTrackingService appointmentTrackingService;

    @Mock
    DateUtils dateUtils;

    @Mock
    UserService userService;

    @Test
    public void when_requestMissingExpectedStartTime_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        appointmentMasterDto.setExpectedStartTime(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<AppointmentMasterDto>> validate = validator.validate(appointmentMasterDto);
        Assert.assertEquals("Không được để trống thông tin thời gian bắt đầu dự kiến", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingBranchId_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        appointmentMasterDto.setBranchId(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<AppointmentMasterDto>> validate = validator.validate(appointmentMasterDto);
        Assert.assertEquals("Không được để trống thông tin chi nhánh", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingCustomer_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        appointmentMasterDto.setCustomer(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<AppointmentMasterDto>> validate = validator.validate(appointmentMasterDto);
        Assert.assertEquals("Không được để trống thông tin khách hàng", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingAppointmentServices_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        appointmentMasterDto.setAppointmentServices(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<AppointmentMasterDto>> validate = validator.validate(appointmentMasterDto);
        Assert.assertEquals("Không được để trống thông tin dịch vụ", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingSpaServiceId_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        appointmentMasterDto.getAppointmentServices().get(0).setSpaServiceId(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<AppointmentMasterDto>> validate = validator.validate(appointmentMasterDto);
        Assert.assertEquals("Không được để trống thông tin dịch vụ spa", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_branchNotExist_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        Mockito.when(branchRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));

        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.createUpdate(appointmentMasterDto));
        Assert.assertEquals("BRANCH_NOT_EXIST", ssdsBusinessException.getCode());
    }

    @Test
    public void when_branchDeactive_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        Branch branch = new Branch();
        branch.setIsActive(false);
        branch.setName("Skin Wisdom Yên Lãng");
        Mockito.when(branchRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(branch));
        Mockito.when(messageSource.getMessage("branch.is.deactive", Arrays.asList(branch.getName()).toArray(), null)).thenReturn("Chi nhánh Skin Wisdom Yên Lãng đã ngưng hoạt động. Vui lòng chọn 1 chi nhánh khác và thử lại");

        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.createUpdate(appointmentMasterDto));
        Assert.assertEquals("Chi nhánh Skin Wisdom Yên Lãng đã ngưng hoạt động. Vui lòng chọn 1 chi nhánh khác và thử lại", ssdsBusinessException.getMessage());
    }

    @Test
    public void when_spaServiceDeactive_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        Branch branch = new Branch();
        branch.setIsActive(true);
        branch.setName("Skin Wisdom Yên Lãng");
        Mockito.when(branchRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(branch));
        Mockito.when(spaServiceRepository.findByIdIn(Mockito.anyList())).thenReturn(initSpaService(Boolean.FALSE));
        Mockito.when(messageSource.getMessage(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn("Các dịch vụ {0} đã ngưng hoạt động. Vui lòng bỏ chọn các dịch vụ này và tiến hành đặt lịch lại.");

        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.createUpdate(appointmentMasterDto));
        Assert.assertEquals("Các dịch vụ {0} đã ngưng hoạt động. Vui lòng bỏ chọn các dịch vụ này và tiến hành đặt lịch lại.", ssdsBusinessException.getMessage());
    }

    @Test
    public void when_expectedStartTimeBeforeNow_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        appointmentMasterDto.setTotal(100D);

        Branch branch = new Branch();
        branch.setIsActive(true);
        branch.setName("Skin Wisdom Yên Lãng");

        Mockito.when(branchRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(branch));
        Mockito.when(spaServiceRepository.findByIdIn(Mockito.anyList())).thenReturn(initSpaService(Boolean.TRUE));
        Mockito.when(priceRepository.getTotalByListService(Mockito.anyList())).thenReturn(100D);
        List<Instant> availableTime = new ArrayList<>();
        availableTime.add(appointmentMasterDto.getExpectedStartTime());
        Mockito.when(appointmentTrackingService.getAvailableTimeByBranch(Mockito.any())).thenReturn(availableTime);
        Mockito.when(messageSource.getMessage("appointment.master.expected.start.time.must.be.after.current.time", null, null)).thenReturn("Thời gian bắt đầu dự kiến không hợp lệ. Thời gian bắt đầu dự kiến không thể trước thời điểm hiện tại");

        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.createUpdate(appointmentMasterDto));
        FieldErrorDTO fieldErrorDTO = (FieldErrorDTO) ssdsBusinessException.getFieldErrorDTOS().get(0);
        Assert.assertEquals("Thời gian bắt đầu dự kiến không hợp lệ. Thời gian bắt đầu dự kiến không thể trước thời điểm hiện tại", fieldErrorDTO.getMessage());
    }

    @Test
    public void when_appointmentDuplicate_should_createUpdateThrowException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
        appointmentMasterDto.setTotal(100D);
        appointmentMasterDto.setExpectedStartTime(Instant.now().plus(1, ChronoUnit.DAYS));

        Branch branch = new Branch();
        branch.setIsActive(true);
        branch.setName("Skin Wisdom Yên Lãng");

        List<AppointmentMaster> appointmentMasters = new ArrayList<>();
        appointmentMasters.add(new AppointmentMaster());

        ReflectionTestUtils.setField(appointmentMasterService, "systemTimezone", "Asia/Ho_Chi_Minh");

        Mockito.when(branchRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(branch));
        Mockito.when(spaServiceRepository.findByIdIn(Mockito.anyList())).thenReturn(initSpaService(Boolean.TRUE));
        Mockito.when(priceRepository.getTotalByListService(Mockito.anyList())).thenReturn(100D);
        List<Instant> availableTime = new ArrayList<>();
        availableTime.add(appointmentMasterDto.getExpectedStartTime());
        Mockito.when(appointmentTrackingService.getAvailableTimeByBranch(Mockito.any())).thenReturn(availableTime);
        Mockito.when(appointmentMasterRepository.findByExpectedStartTimeAndCustomerIdAndStatusCodeNotIn(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(appointmentMasters);
        Mockito.when(userService.getCustomer(Mockito.any())).thenReturn(new User());

        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.createUpdate(appointmentMasterDto));
        Assert.assertEquals("APPOINTMENT_MASTER_DUPLICATE", ssdsBusinessException.getCode());
    }

    private List<SpaService> initSpaService(Boolean isActive) {
        List<SpaService> services = new ArrayList<>();
        SpaService spaService = new SpaService();
        spaService.setId(1L);
        spaService.setIsActive(isActive);
        spaService.setDuration(60L);
        services.add(spaService);
        return services;
    }

//    @Test
//    public void when_branchDeactive1_should_createUpdateThrowException() {
//        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterForCreate();
//        Branch branch = new Branch();
//        branch.setIsActive(true);
//        branch.setName("Skin Wisdom Yên Lãng");
//        branch.setCode("SKIN_WISDOM_YEN_LANG");
//        Mockito.when(branchRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(branch));
//
//        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.createUpdate(appointmentMasterDto));
//        Assert.assertEquals("Chi nhánh Skin Wisdom Yên Lãng đã ngưng hoạt động. Vui lòng chọn 1 chi nhánh khác và thử lại", ssdsBusinessException.getMessage());
//    }

    @Test
    public void when_appointmentMasterHasInvalidStatus_should_throwException() {
        ConfirmAppointmentRequestDto requestDto = new ConfirmAppointmentRequestDto();
        requestDto.setApptMasterId(1L);
        requestDto.setAction(Constants.ACTION_CONFIRM.CONFIRM);

        AppointmentMaster appointmentMaster = initReadyAppointmentMaster();

        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(appointmentMaster));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.confirmAppointments(Arrays.asList(requestDto)));
        Assert.assertEquals(ssdsBusinessException.getMessage(), "Trạng thái lịch hẹn 1 không hợp lệ. Vui lòng kiểm tra và thử lại");
    }

    @Test
    public void when_chooseActionCancelAppointmentWithoutReason_should_throwException() {
        ConfirmAppointmentRequestDto requestDto = new ConfirmAppointmentRequestDto();
        requestDto.setApptMasterId(1L);
        requestDto.setAction(Constants.ACTION_CONFIRM.CANCEL);

        AppointmentMaster appointmentMaster = initWaitingForConfirmAppointmentMaster();

        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(appointmentMaster));
        Mockito.when(reasonMessageRepository.findAllActiveReasonMessageByIdAndReasonMessageTypeCode(Mockito.anyList(), Mockito.anyString())).thenReturn(new ArrayList<ReasonMessage>());
        Mockito.when(messageSource.getMessage("appointment.master.cancel.reason.required", null, null)).thenReturn("Vui lòng điền lí do hủy lịch hẹn.");
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.confirmAppointments(Arrays.asList(requestDto)));

        Assert.assertEquals("INPUT_INVALID", ssdsBusinessException.getCode());
        Assert.assertEquals(ssdsBusinessException.getFieldErrorDTOS().size(), 1);
        FieldErrorDTO errorDTO = (FieldErrorDTO) ssdsBusinessException.getFieldErrorDTOS().get(0);
        Assert.assertEquals("Vui lòng điền lí do hủy lịch hẹn.", errorDTO.getMessage());
        Assert.assertEquals("reasonMessage", errorDTO.getField());
        System.out.println();
    }

    @Test
    public void when_chooseActionCancelAppointmentWithInvalidReason_should_throwException() {
        ConfirmAppointmentRequestDto requestDto = new ConfirmAppointmentRequestDto();
        requestDto.setApptMasterId(1L);
        requestDto.setAction(Constants.ACTION_CONFIRM.CANCEL);

        ReasonMessageDto reasonMessageDto = new ReasonMessageDto();
        reasonMessageDto.setId(1L);
        requestDto.setCanceledReason(reasonMessageDto);

        AppointmentMaster appointmentMaster = initWaitingForConfirmAppointmentMaster();

        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(appointmentMaster));
        Mockito.when(reasonMessageRepository.findAllActiveReasonMessageByIdAndReasonMessageTypeCode(Mockito.anyList(), Mockito.anyString())).thenReturn(initListReasonMessage());
        Mockito.when(messageSource.getMessage("appointment.master.cancel.reason.not.exist", null, null)).thenReturn("Lý do hủy lịch hẹn không tồn tại. Vui lòng thử lại.");
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.confirmAppointments(Arrays.asList(requestDto)));

        Assert.assertEquals("INPUT_INVALID", ssdsBusinessException.getCode());
        Assert.assertEquals(ssdsBusinessException.getFieldErrorDTOS().size(), 1);
        FieldErrorDTO errorDTO = (FieldErrorDTO) ssdsBusinessException.getFieldErrorDTOS().get(0);
        Assert.assertEquals("Lý do hủy lịch hẹn không tồn tại. Vui lòng thử lại.", errorDTO.getMessage());
        Assert.assertEquals("reasonMessage", errorDTO.getField());
    }

    @Test
    public void when_chooseActionCancelAppointmentWithRequireNoteReasonButNoteIsNull_should_throwException() {
        ConfirmAppointmentRequestDto requestDto = new ConfirmAppointmentRequestDto();
        requestDto.setApptMasterId(1L);
        requestDto.setAction(Constants.ACTION_CONFIRM.CANCEL);

        ReasonMessageDto reasonMessageDto = new ReasonMessageDto();
        reasonMessageDto.setId(3L);
        requestDto.setCanceledReason(reasonMessageDto);

        AppointmentMaster appointmentMaster = initWaitingForConfirmAppointmentMaster();

        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(appointmentMaster));
        Mockito.when(reasonMessageRepository.findAllActiveReasonMessageByIdAndReasonMessageTypeCode(Mockito.anyList(), Mockito.anyString())).thenReturn(initListReasonMessage());
        Mockito.when(messageSource.getMessage("appointment.master.cancel.reason.require.note", null, null)).thenReturn("Ghi chú là thông tin bắt buộc với lý do này.");
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.confirmAppointments(Arrays.asList(requestDto)));

        Assert.assertEquals("INPUT_INVALID", ssdsBusinessException.getCode());
        Assert.assertEquals(ssdsBusinessException.getFieldErrorDTOS().size(), 1);
        FieldErrorDTO errorDTO = (FieldErrorDTO) ssdsBusinessException.getFieldErrorDTOS().get(0);
        Assert.assertEquals("Ghi chú là thông tin bắt buộc với lý do này.", errorDTO.getMessage());
        Assert.assertEquals("reasonNote", errorDTO.getField());
    }

    @Test
    public void when_cancelListApptWithoutCancelReason_should_throwException() {
        AppointmentMasterDto appointmentMasterDto = new AppointmentMasterDto();
        appointmentMasterDto.setId(1L);

        Mockito.when(messageSource.getMessage("appointment.master.cancel.reason.required", null, null)).thenReturn("Vui lòng điền lí do hủy lịch hẹn.");
        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(initAppointmentMasterWithStatus(Constants.APPOINTMENT_MASTER_STATUS.READY)));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.cancelAppointmentMaster(Arrays.asList(appointmentMasterDto)));
        Assert.assertEquals(ssdsBusinessException.getCode(), "INPUT_INVALID");
        Assert.assertEquals(ssdsBusinessException.getFieldErrorDTOS().size(), 1);
        FieldErrorDTO errorDTO = (FieldErrorDTO) ssdsBusinessException.getFieldErrorDTOS().get(0);
        Assert.assertEquals(errorDTO.getMessage(), "Vui lòng điền lí do hủy lịch hẹn.");
        Assert.assertEquals(errorDTO.getField(), "canceledReason");
    }

    @Test
    public void when_cancelCanceledAppt_should_throwInvalidStatusException() {
        AppointmentMasterDto appointmentMasterDto = new AppointmentMasterDto();
        appointmentMasterDto.setId(1L);

        Mockito.when(messageSource.getMessage("appointment.master.cancel.invalid.status", null, null)).thenReturn("Không thể hủy các lịch hẹn có trạng thái  Đã hủy, Hoàn thành, Đã đóng. Vui lòng bỏ chọn các lịch hẹn không hợp lệ {0} và thử lại.");
        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(initAppointmentMasterWithStatus(Constants.APPOINTMENT_MASTER_STATUS.CANCELED)));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.cancelAppointmentMaster(Arrays.asList(appointmentMasterDto)));
        Assert.assertEquals(ssdsBusinessException.getMessage(), "Không thể hủy các lịch hẹn có trạng thái  Đã hủy, Hoàn thành, Đã đóng. Vui lòng bỏ chọn các lịch hẹn không hợp lệ  và thử lại.");
    }

    @Test
    public void when_cancelClosedAppt_should_throwInvalidStatusException() {
        AppointmentMasterDto appointmentMasterDto = new AppointmentMasterDto();
        appointmentMasterDto.setId(1L);

        Mockito.when(messageSource.getMessage("appointment.master.cancel.invalid.status", null, null)).thenReturn("Không thể hủy các lịch hẹn có trạng thái  Đã hủy, Hoàn thành, Đã đóng. Vui lòng bỏ chọn các lịch hẹn không hợp lệ {0} và thử lại.");
        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(initAppointmentMasterWithStatus(Constants.APPOINTMENT_MASTER_STATUS.CLOSED)));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.cancelAppointmentMaster(Arrays.asList(appointmentMasterDto)));
        Assert.assertEquals(ssdsBusinessException.getMessage(), "Không thể hủy các lịch hẹn có trạng thái  Đã hủy, Hoàn thành, Đã đóng. Vui lòng bỏ chọn các lịch hẹn không hợp lệ  và thử lại.");
    }

    @Test
    public void when_cancelCompletedAppt_should_throwInvalidStatusException() {
        AppointmentMasterDto appointmentMasterDto = new AppointmentMasterDto();
        appointmentMasterDto.setId(1L);

        Mockito.when(messageSource.getMessage("appointment.master.cancel.invalid.status", null, null)).thenReturn("Không thể hủy các lịch hẹn có trạng thái  Đã hủy, Hoàn thành, Đã đóng. Vui lòng bỏ chọn các lịch hẹn không hợp lệ {0} và thử lại.");
        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(initAppointmentMasterWithStatus(Constants.APPOINTMENT_MASTER_STATUS.COMPLETED)));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.cancelAppointmentMaster(Arrays.asList(appointmentMasterDto)));
        Assert.assertEquals(ssdsBusinessException.getMessage(), "Không thể hủy các lịch hẹn có trạng thái  Đã hủy, Hoàn thành, Đã đóng. Vui lòng bỏ chọn các lịch hẹn không hợp lệ  và thử lại.");
    }

    @Test
    public void when_cancelListApptWithNotExistedCancelReason_should_throwException() {
        AppointmentMasterDto appointmentMasterDto = new AppointmentMasterDto();
        appointmentMasterDto.setId(1L);
        ReasonMessageDto reasonMessageDto = new ReasonMessageDto();
        reasonMessageDto.setId(1L);
        reasonMessageDto.setRequireNote(false);
        appointmentMasterDto.setCanceledReason(reasonMessageDto);

        Mockito.when(messageSource.getMessage("appointment.master.cancel.reason.not.exist", null, null)).thenReturn("Lý do hủy lịch hẹn không tồn tại. Vui lòng thử lại.");
        Mockito.when(reasonMessageRepository.findAllActiveReasonMessageByIdAndReasonMessageTypeCode(Mockito.anyList(), Mockito.anyString())).thenReturn(new ArrayList<>());
        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(initAppointmentMasterWithStatus(Constants.APPOINTMENT_MASTER_STATUS.READY)));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.cancelAppointmentMaster(Arrays.asList(appointmentMasterDto)));
        Assert.assertEquals(ssdsBusinessException.getCode(), "INPUT_INVALID");
        Assert.assertEquals(ssdsBusinessException.getFieldErrorDTOS().size(), 1);
        FieldErrorDTO errorDTO = (FieldErrorDTO) ssdsBusinessException.getFieldErrorDTOS().get(0);
        Assert.assertEquals(errorDTO.getMessage(), "Lý do hủy lịch hẹn không tồn tại. Vui lòng thử lại.");
        Assert.assertEquals(errorDTO.getField(), "canceledReason");
    }

    @Test
    public void when_cancelApptWithRequiredNoteCancelReasonButNoteIsNull_should_throwException() {
        AppointmentMasterDto appointmentMasterDto = new AppointmentMasterDto();
        appointmentMasterDto.setId(1L);
        ReasonMessageDto reasonMessageDto = new ReasonMessageDto();
        reasonMessageDto.setId(1L);
        reasonMessageDto.setRequireNote(true);
        appointmentMasterDto.setCanceledReason(reasonMessageDto);

        ReasonMessage reasonMessage = new ReasonMessage();
        reasonMessage.setId(1L);
        reasonMessage.setRequireNote(true);

        Mockito.when(messageSource.getMessage("appointment.master.cancel.reason.require.note", null, null)).thenReturn("Ghi chú là thông tin bắt buộc với lý do này.");
        Mockito.when(reasonMessageRepository.findAllActiveReasonMessageByIdAndReasonMessageTypeCode(Mockito.anyList(), Mockito.anyString())).thenReturn(Arrays.asList(reasonMessage));
        Mockito.when(appointmentMasterRepository.findAllById(Mockito.anyList())).thenReturn(Arrays.asList(initAppointmentMasterWithStatus(Constants.APPOINTMENT_MASTER_STATUS.READY)));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.cancelAppointmentMaster(Arrays.asList(appointmentMasterDto)));
        Assert.assertEquals(ssdsBusinessException.getCode(), "INPUT_INVALID");
        Assert.assertEquals(ssdsBusinessException.getFieldErrorDTOS().size(), 1);
        FieldErrorDTO errorDTO = (FieldErrorDTO) ssdsBusinessException.getFieldErrorDTOS().get(0);
        Assert.assertEquals(errorDTO.getMessage(), "Ghi chú là thông tin bắt buộc với lý do này.");
        Assert.assertEquals(errorDTO.getField(), "canceledNote");
    }

    @Test
    public void when_requestMissingAmId_should_confirmAppointmentThrowException() {
        ConfirmAppointmentRequestDto requestDto = new ConfirmAppointmentRequestDto();
        requestDto.setAction(Constants.ACTION_CONFIRM.CONFIRM);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<ConfirmAppointmentRequestDto>> validate = validator.validate(requestDto);
        Assert.assertEquals("Không được để trống thông mã lịch hẹn", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingAction_should_confirmAppointmentThrowException() {
        ConfirmAppointmentRequestDto requestDto = new ConfirmAppointmentRequestDto();
        requestDto.setApptMasterId(1L);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<ConfirmAppointmentRequestDto>> validate = validator.validate(requestDto);
        Assert.assertEquals("Vui lòng lựa chọn hành động để tiếp tục", validate.stream().findFirst().get().getMessage());
    }

    private AppointmentMaster initAppointmentMasterWithStatus(String statusCode) {
        AppointmentMaster appointmentMaster = new AppointmentMaster();
        Lookup status = new Lookup();
        status.setId(1L);
        status.setCode(statusCode);
        appointmentMaster.setStatus(status);
        return appointmentMaster;
    }

    private AppointmentMasterDto initAppointmentMasterDtoWithStatus(String statusCode) {
        AppointmentMasterDto appointmentMaster = new AppointmentMasterDto();
        LookupDto status = new LookupDto();
        status.setId(1L);
        status.setCode(statusCode);
        appointmentMaster.setStatus(status);
        return appointmentMaster;
    }

    private AppointmentMaster initWaitingForConfirmAppointmentMaster() {
        Lookup apptMasterStatus = new Lookup();
        apptMasterStatus.setId(1L);
        apptMasterStatus.setLookupKey(Constants.LOOKUP_KEY.APPOINTMENT_MASTER_STATUS);
        apptMasterStatus.setCode(Constants.APPOINTMENT_MASTER_STATUS.WAITING_FOR_CONFIRMATION);

        Lookup apptServiceStatus = new Lookup();
        apptServiceStatus.setId(1L);
        apptServiceStatus.setLookupKey(Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS);
        apptServiceStatus.setCode(Constants.APPOINTMENT_SERVICE_STATUS.WAITING_FOR_CONFIRMATION);

        AppointmentMaster appointmentMaster = new AppointmentMaster();
        appointmentMaster.setId(1L);
        appointmentMaster.setStatus(apptMasterStatus);

        AppointmentService appointmentService = new AppointmentService();
        appointmentService.setId(1L);
        appointmentService.setStatus(apptServiceStatus);
        appointmentService.setAppointmentMaster(appointmentMaster);

        List<AppointmentService> services = new ArrayList<>();
        services.add(appointmentService);
        appointmentMaster.setAppointmentServices(services);

        return appointmentMaster;
    }

    private AppointmentMaster initReadyAppointmentMaster() {
        Lookup apptMasterStatus = new Lookup();
        apptMasterStatus.setId(1L);
        apptMasterStatus.setLookupKey(Constants.LOOKUP_KEY.APPOINTMENT_MASTER_STATUS);
        apptMasterStatus.setCode(Constants.APPOINTMENT_MASTER_STATUS.READY);

        Lookup apptServiceStatus = new Lookup();
        apptServiceStatus.setId(1L);
        apptServiceStatus.setLookupKey(Constants.LOOKUP_KEY.APPOINTMENT_SERVICE_STATUS);
        apptServiceStatus.setCode(Constants.APPOINTMENT_SERVICE_STATUS.READY);

        AppointmentMaster appointmentMaster = new AppointmentMaster();
        appointmentMaster.setId(1L);
        appointmentMaster.setStatus(apptMasterStatus);

        AppointmentService appointmentService = new AppointmentService();
        appointmentService.setId(1L);
        appointmentService.setStatus(apptServiceStatus);
        appointmentService.setAppointmentMaster(appointmentMaster);

        List<AppointmentService> services = new ArrayList<>();
        services.add(appointmentService);
        appointmentMaster.setAppointmentServices(services);

        return appointmentMaster;
    }

    private List<ReasonMessage> initListReasonMessage() {
        ReasonMessage reasonMessage = new ReasonMessage();
        reasonMessage.setId(2L);
        reasonMessage.setRequireNote(false);

        ReasonMessage reasonMessage1 = new ReasonMessage();
        reasonMessage.setId(3L);
        reasonMessage.setRequireNote(true);
        return Arrays.asList(reasonMessage, reasonMessage1);
    }

    private AppointmentMasterDto initAppointmentMasterForCreate() {
        AppointmentMasterDto appointmentMasterDto = new AppointmentMasterDto();
        appointmentMasterDto.setExpectedStartTime(Instant.now());
        appointmentMasterDto.setBranchId(1L);

        UserListingDTO customer = new UserListingDTO();
        customer.setPhoneNumber("0386759483");
        appointmentMasterDto.setCustomer(customer);

        List<AppointmentServiceDto> serviceDtos = new ArrayList<>();
        AppointmentServiceDto service = new AppointmentServiceDto();
        service.setSpaServiceId(1L);
        serviceDtos.add(service);
        appointmentMasterDto.setAppointmentServices(serviceDtos);

        return appointmentMasterDto;
    }

    @Test
    public void when_checkinAmHaveInvalidStatus_should_throwException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterDtoWithStatus(Constants.APPOINTMENT_MASTER_STATUS.COMPLETED);
        AppointmentMaster appointmentMaster = initAppointmentMasterWithStatus(Constants.APPOINTMENT_MASTER_STATUS.COMPLETED);

        Mockito.when(messageSource.getMessage("appointment.master.checkin.invalid.status", null, null)).thenReturn("Chỉ có thể thực hiện checkin với lịch hẹn có trạng thái Chờ xác nhận hoặc Chờ thực hiện.");
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.checkin(appointmentMaster, appointmentMasterDto));
        Assert.assertEquals(ssdsBusinessException.getMessage(), "Chỉ có thể thực hiện checkin với lịch hẹn có trạng thái Chờ xác nhận hoặc Chờ thực hiện.");
    }

    @Test
    public void when_checkinAmWhichNoAsHaveActualStartTime_should_throwException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterDtoWithStatus(Constants.APPOINTMENT_MASTER_STATUS.READY);
        AppointmentServiceDto serviceDto = new AppointmentServiceDto();
        serviceDto.setId(1L);
        List<AppointmentServiceDto> serviceDtos = new ArrayList<>();
        serviceDtos.add(serviceDto);
        appointmentMasterDto.setAppointmentServices(serviceDtos);

        AppointmentMaster appointmentMaster = initAppointmentMasterWithStatus(Constants.APPOINTMENT_MASTER_STATUS.READY);

        Mockito.when(messageSource.getMessage("appointment.master.checkin.required.at.least.one.service.has.actual.start.time", null, null)).thenReturn("Vui lòng điền thời gian bắt đầu thực tế cho ít nhất một dịch vụ");
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.checkin(appointmentMaster, appointmentMasterDto));
        Assert.assertEquals("Vui lòng điền thời gian bắt đầu thực tế cho ít nhất một dịch vụ", ssdsBusinessException.getMessage());
    }

    @Test
    public void when_checkinAmWhichNoAsHaveSpecialistInfo_should_throwException() {
        AppointmentMasterDto appointmentMasterDto = initAppointmentMasterDtoWithStatus(Constants.APPOINTMENT_MASTER_STATUS.READY);
        AppointmentServiceDto serviceDto = new AppointmentServiceDto();
        serviceDto.setId(1L);
        serviceDto.setActualStartTime(Instant.now());
        List<AppointmentServiceDto> serviceDtos = new ArrayList<>();
        serviceDtos.add(serviceDto);
        appointmentMasterDto.setAppointmentServices(serviceDtos);

        AppointmentMaster appointmentMaster = initAppointmentMasterWithStatus(Constants.APPOINTMENT_MASTER_STATUS.READY);

        Mockito.when(messageSource.getMessage(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("Vui lòng điền thông tin chuyên viên thực hiện hoặc ghi chú về chuyên viên (nếu có nhiều chuyên viên thay phiên thực hiện) cho các dịch vụ {0}");
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> appointmentMasterService.checkin(appointmentMaster, appointmentMasterDto));
        Assert.assertEquals("Vui lòng điền thông tin chuyên viên thực hiện hoặc ghi chú về chuyên viên (nếu có nhiều chuyên viên thay phiên thực hiện) cho các dịch vụ {0}", ssdsBusinessException.getMessage());
    }
    
}
