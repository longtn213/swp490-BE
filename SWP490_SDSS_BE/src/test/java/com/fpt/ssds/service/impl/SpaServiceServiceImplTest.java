package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.domain.SpaService;
import com.fpt.ssds.repository.SpaServiceRepository;
import com.fpt.ssds.service.FileService;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.service.mapper.SpaServiceMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class SpaServiceServiceImplTest {
    @InjectMocks
    SpaServiceServiceImpl spaServiceService;

    @Mock
    SpaServiceRepository spaServiceRepository;

    @Mock
    SpaServiceMapper spaServiceMapper;

    @Mock
    FileService fileService;

    @Test
    public void when_requestMissingName_should_createUpdateThrowException() {
        SpaServiceDto spaServiceDto = initSpaService();
        spaServiceDto.setName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<SpaServiceDto>> validate = validator.validate(spaServiceDto);
        Assert.assertEquals("Không được để trống tên dịch vụ", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_nameIsEmpty_should_createUpdateThrowException() {
        SpaServiceDto spaServiceDto = initSpaService();
        spaServiceDto.setName("");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<SpaServiceDto>> validate = validator.validate(spaServiceDto);
        Assert.assertEquals("Không được để trống tên dịch vụ", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_durationIsMissing_should_createUpdateThrowException() {
        SpaServiceDto spaServiceDto = initSpaService();
        spaServiceDto.setDuration(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<SpaServiceDto>> validate = validator.validate(spaServiceDto);
        Assert.assertEquals("Không được để trống thời gian thực hiện", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_durationIsLessThan0_should_createUpdateThrowException() {
        SpaServiceDto spaServiceDto = initSpaService();
        spaServiceDto.setDuration(-1L);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<SpaServiceDto>> validate = validator.validate(spaServiceDto);
        Assert.assertEquals("Thời gian thực hiện không thể là số âm", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_currentPriceIsNull_should_createUpdateThrowException() {
        SpaServiceDto spaServiceDto = initSpaService();
        spaServiceDto.setCurrentPrice(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<SpaServiceDto>> validate = validator.validate(spaServiceDto);
        Assert.assertEquals("Không được để trống giá", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_currentPriceIsLessThan0_should_createUpdateThrowException() {
        SpaServiceDto spaServiceDto = initSpaService();
        spaServiceDto.setCurrentPrice(-1D);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<SpaServiceDto>> validate = validator.validate(spaServiceDto);
        Assert.assertEquals("Giá tiền không thể là số âm", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_serviceIsNotExist_should_getByIdThrException() {
        Mockito.when(spaServiceRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> spaServiceService.getById(1L));
        Assert.assertEquals("SPA_SERVICE_NOT_EXIST", ssdsBusinessException.getCode());
    }

    @Test
    public void when_serviceIsExist_should_getByIdThrException() {
        SpaService spaService = new SpaService();
        SpaServiceDto spaServiceDto = new SpaServiceDto();
        Mockito.when(spaServiceRepository.findById(1L)).thenReturn(Optional.of(spaService));
        Mockito.when(spaServiceMapper.toDto(spaService)).thenReturn(spaServiceDto);
        Mockito.when(fileService.findByTypeAndRefIdAndUploadStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
        Assert.assertEquals(spaServiceDto, spaServiceService.getById(1L));
    }

    private SpaServiceDto initSpaService() {
        SpaServiceDto serviceDto = new SpaServiceDto();
        serviceDto.setName("Service name");
        serviceDto.setDuration(60L);
        serviceDto.setCurrentPrice(1000000D);
        return serviceDto;
    }

}
