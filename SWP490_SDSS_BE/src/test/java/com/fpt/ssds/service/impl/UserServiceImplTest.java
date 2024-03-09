package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.*;
import com.fpt.ssds.repository.AppointmentServiceRepository;
import com.fpt.ssds.repository.BranchRepository;
import com.fpt.ssds.repository.RoleRepository;
import com.fpt.ssds.repository.UserRepository;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.service.dto.UserDto;
import com.fpt.ssds.service.dto.UserListingDTO;
import com.fpt.ssds.service.mapper.*;
import com.fpt.ssds.utils.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    AppointmentServiceRepository appointmentServiceRepository;

    @Mock
    BranchRepository branchRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private UserListingMapper userListingMapper = Mappers.getMapper(UserListingMapper.class);

    @Spy
    private RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    @Spy
    private BranchMapper branchMapper = Mappers.getMapper(BranchMapper.class);

    @Mock
    MessageSource messageSource;

    @Mock
    RoleRepository roleRepository;

    @Before
    public void before() {
        ReflectionTestUtils.setField(userMapper, "branchMapper", branchMapper);
        ReflectionTestUtils.setField(userMapper, "roleMapper", roleMapper);
    }


    @Test
    public void should_getListAvalableSpecialistByTime_successful() {
        ReflectionTestUtils.setField(userService, "systemTimezone", "Asia/Ho_Chi_Minh");
        long startTime = ZonedDateTime.now().toEpochSecond();
        long endTime = ZonedDateTime.now().plus(2, ChronoUnit.HOURS).toEpochSecond();
        Map<String, Instant> instantMap = DateUtils.zonedDateTimeToInstant(
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.of("Asia/Ho_Chi_Minh")),
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.of("Asia/Ho_Chi_Minh")),
            "Asia/Ho_Chi_Minh");
        Instant instantStartTime = instantMap.get("startTime");
        Instant instantEndTime = instantMap.get("endTime");

        Mockito.when(branchRepository.findById(1L)).thenReturn(Optional.of(new Branch()));
        Mockito.when(userRepository.findByRoleAndBranch(Constants.ROLE.SPECIALIST, 1L)).thenReturn(initListAllSpecialist());
//        Mockito.when(userRepository.findSpecialistNotAvailableByTime(instantStartTime, instantEndTime, 1L)).thenReturn(initListNotAvailableSpecialist());
        Mockito.when(appointmentServiceRepository.findById(1L)).thenReturn(Optional.of(initApptService()));
        List<UserListingDTO> userDtos = userService.getListAvailableSpecialistByTime(startTime, endTime, 1L, 1L);

        Assert.assertEquals(3, userDtos.size());
    }

    @Test
    public void should_getListAvalableSpecialistByTime_successful1() {
        ReflectionTestUtils.setField(userService, "systemTimezone", "Asia/Ho_Chi_Minh");
        User user = new User();
        user.setId(2L);

        AppointmentService appointmentService = initApptService();
        appointmentService.setSpecialist(user);

        long startTime = ZonedDateTime.now().toEpochSecond();
        long endTime = ZonedDateTime.now().plus(2, ChronoUnit.HOURS).toEpochSecond();
        Map<String, Instant> instantMap = DateUtils.zonedDateTimeToInstant(
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.of("Asia/Ho_Chi_Minh")),
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.of("Asia/Ho_Chi_Minh")),
            "Asia/Ho_Chi_Minh");
        Instant instantStartTime = instantMap.get("startTime");
        Instant instantEndTime = instantMap.get("endTime");

        Mockito.when(branchRepository.findById(1L)).thenReturn(Optional.of(new Branch()));
        Mockito.when(userRepository.findByRoleAndBranch(Constants.ROLE.SPECIALIST, 1L)).thenReturn(initListAllSpecialist());
//        Mockito.when(userRepository.findSpecialistNotAvailableByTime(instantStartTime, instantEndTime, 1L)).thenReturn(initListNotAvailableSpecialist());
        Mockito.when(appointmentServiceRepository.findById(1L)).thenReturn(Optional.of(appointmentService));
        List<UserListingDTO> userDtos = userService.getListAvailableSpecialistByTime(startTime, endTime, 1L, 1L);

        Assert.assertEquals(3, userDtos.size());
    }

    @Test
    public void when_branchNotExist_should_getListAvalableSpecialistByTime_throwException() {
        ReflectionTestUtils.setField(userService, "systemTimezone", "Asia/Ho_Chi_Minh");
        long startTime = ZonedDateTime.now().toEpochSecond();
        long endTime = ZonedDateTime.now().plus(2, ChronoUnit.HOURS).toEpochSecond();
        Map<String, Instant> instantMap = DateUtils.zonedDateTimeToInstant(
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.of("Asia/Ho_Chi_Minh")),
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.of("Asia/Ho_Chi_Minh")),
            "Asia/Ho_Chi_Minh");

        Mockito.when(branchRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> userService.getListAvailableSpecialistByTime(startTime, endTime, 1L, 1L));
        Assert.assertEquals(ssdsBusinessException.getCode(), "BRANCH_NOT_EXIST");
    }

    @Test
    public void when_phoneNumberIsMissing_should_createUpdateThrowException() {
        UserDto userDto = initUserDto();
        userDto.setPhoneNumber(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDto>> validate = validator.validate(userDto);
        Assert.assertEquals("Vui lòng không để trống số điện thoại.", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_phoneNumberIsEmpty_should_createUpdateThrowException() {
        UserDto userDto = initUserDto();
        userDto.setPhoneNumber("");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDto>> validate = validator.validate(userDto);
        Assert.assertEquals("Vui lòng không để trống số điện thoại.", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_usernameIsMissing_should_createUpdateThrowException() {
        UserDto userDto = initUserDto();
        userDto.setUsername(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDto>> validate = validator.validate(userDto);
        Assert.assertEquals("Vui lòng không để trống tên đăng nhập.", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_usernameIsEmpty_should_createUpdateThrowException() {
        UserDto userDto = initUserDto();
        userDto.setUsername("");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDto>> validate = validator.validate(userDto);
        Assert.assertEquals("Vui lòng không để trống tên đăng nhập.", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_passwordIsMissing_should_createUpdateThrowException() {
        UserDto userDto = initUserDto();
        userDto.setPassword(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDto>> validate = validator.validate(userDto);
        Assert.assertEquals("Vui lòng không để trống mật khẩu.", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_passwordIsEmpty_should_createUpdateThrowException() {
        UserDto userDto = initUserDto();
        userDto.setPassword("");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDto>> validate = validator.validate(userDto);
        Assert.assertEquals("Vui lòng không để trống mật khẩu.", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_ABC_should_createUpdateThrowException() {
        UserDto userDto = initUserDto();

        Mockito.when(userRepository.findByPhoneNumberAndRefIdIsNull(Mockito.any())).thenReturn(Optional.ofNullable(null));
        Mockito.when(roleRepository.findByCode(Constants.ROLE.CUSTOMER)).thenReturn(Optional.of(initRole(Constants.ROLE.CUSTOMER)));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> userService.createUser(userDto));
    }

    private Role initRole(String code) {
        Role role = new Role();
        role.setId(1L);
        role.setCode(code);
        return role;
    }

    private UserDto initUserDto() {
        UserDto userDto = new UserDto();
        userDto.setPhoneNumber("0368195864");
        userDto.setUsername("trinhpham2929");
        userDto.setPassword("Abc@12345");
        return userDto;
    }

    private AppointmentService initApptService() {
        AppointmentService service = new AppointmentService();
        service.setId(1L);
        return service;
    }

    private List<User> initListNotAvailableSpecialist() {
        List<User> specialists = new ArrayList<>();

        User s1 = new User();
        s1.setId(1L);
        specialists.add(s1);
        return specialists;
    }

    private List<User> initListAllSpecialist() {
        List<User> specialists = new ArrayList<>();

        User s1 = new User();
        s1.setId(1L);
        specialists.add(s1);

        User s2 = new User();
        s2.setId(2L);
        specialists.add(s2);

        User s3 = new User();
        s3.setId(3L);
        specialists.add(s3);
        return specialists;
    }
}
