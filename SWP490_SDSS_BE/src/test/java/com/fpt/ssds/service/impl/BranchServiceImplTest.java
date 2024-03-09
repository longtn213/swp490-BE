package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.domain.Location;
import com.fpt.ssds.repository.AppointmentMasterRepository;
import com.fpt.ssds.repository.BranchRepository;
import com.fpt.ssds.repository.LocationRepository;
import com.fpt.ssds.service.ConfigDataService;
import com.fpt.ssds.service.FileService;
import com.fpt.ssds.service.dto.BranchDto;
import com.fpt.ssds.service.dto.LocationDTO;
import com.fpt.ssds.service.mapper.BranchMapper;
import com.fpt.ssds.service.mapper.LocationMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class BranchServiceImplTest {
    @InjectMocks
    BranchServiceImpl branchService;

    @Mock
    BranchRepository branchRepository;

    @Mock
    LocationRepository locationRepository;

    @Mock
    MessageSource messageSource;

    @Mock
    ConfigDataService configDataService;

    @Mock
    FileService fileService;

    @Mock
    AppointmentMasterRepository appointmentMasterRepository;

    @Spy
    private BranchMapper branchMapper = Mappers.getMapper(BranchMapper.class);

    @Spy
    private LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

    @Before
    public void before() {
        ReflectionTestUtils.setField(branchMapper, "locationMapper", locationMapper);
    }

    @Test
    public void when_requestMissingBranchName_should_createUpdateThrowException() {
        BranchDto branchDto = initBranchDTO();
        branchDto.setName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<BranchDto>> validate = validator.validate(branchDto);
        Assert.assertEquals("Không được để trống tên cơ sở", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingBranchDetailAddress_should_createUpdateThrowException() {
        BranchDto branchDto = initBranchDTO();
        branchDto.setDetailAddress(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<BranchDto>> validate = validator.validate(branchDto);
        Assert.assertEquals("Không được để trống địa chỉ cơ sở", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingBranchHotline_should_createUpdateThrowException() {
        BranchDto branchDto = initBranchDTO();
        branchDto.setHotline(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<BranchDto>> validate = validator.validate(branchDto);
        Assert.assertEquals("Không được để trống số điện thoại", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingState_should_createUpdateThrowException() {
        BranchDto branchDto = initBranchDTO();
        branchDto.setState(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<BranchDto>> validate = validator.validate(branchDto);
        Assert.assertEquals("Không được để trống thông tin tỉnh/thành", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingcCity_should_createUpdateThrowException() {
        BranchDto branchDto = initBranchDTO();
        branchDto.setCity(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<BranchDto>> validate = validator.validate(branchDto);
        Assert.assertEquals("Không được để trống thông tin quận/huyện", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_requestMissingDistrict_should_createUpdateThrowException() {
        BranchDto branchDto = initBranchDTO();
        branchDto.setDistrict(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        Set<ConstraintViolation<BranchDto>> validate = validator.validate(branchDto);
        Assert.assertEquals("Không được để trống thông tin phường/xã", validate.stream().findFirst().get().getMessage());
    }

    @Test
    public void when_coordinateDuplicate_should_createUpdateThrowException() {
        BranchDto branchDto = initBranchDTO();
        Branch branch = initBranch();
        branch.setId(1L);
        Mockito.when(locationRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(initDistrict()));
        Mockito.when(branchRepository.findByLatitudeAndLongitude(Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(Optional.of(branch));
        Mockito.when(messageSource.getMessage("branch.coordinate.already.exist", null, null)).thenReturn("Chi nhánh {0} có địa chỉ là {1} đã tồn tại");

        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> branchService.createUpdate(branchDto));
        Assert.assertEquals("Chi nhánh Skin Wisdom Yên Lãng có địa chỉ là 15 P.Yên Lãng, Phường Phương Liệt, Quận Đống Đa, Hà Nội đã tồn tại",
            ssdsBusinessException.getMessage());
    }

    @Test
    public void when_requestIsValid_should_createUpdateSuccessfull() {
        BranchDto branchDto = initBranchDTO();
        Branch branch = initBranch();
        List<Location> locationList = initListLocation();

        Mockito.when(locationRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(initDistrict()));
        Mockito.when(branchRepository.findByLatitudeAndLongitude(Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(Optional.ofNullable(null));
        Mockito.when(locationRepository.findAllById(Mockito.anyList())).thenReturn(locationList);
        branchService.createUpdate(branchDto);
        Mockito.verify(branchRepository, Mockito.times(1)).save(Mockito.any());

    }

    @Test
    public void when_branchHaveAmNotStart_should_createUpdateSuccessfull() {
        BranchDto branchDto = initBranchDTO();
        branchDto.setId(1L);
        branchDto.setIsActive(false);
        Branch branch = initBranch();
        branch.setId(1L);
        branch.setIsActive(true);
        List<Location> locationList = initListLocation();

        Mockito.when(branchRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(branch));
        Mockito.when(appointmentMasterRepository.countByBranchIdAndStatusCodeIn(Mockito.anyLong(), Mockito.anyList())).thenReturn(Integer.valueOf(2));
        Mockito.when(messageSource.getMessage("branch.deactive.have.not.been.started.am", null, null)).thenReturn("Không thể khóa chi nhánh vì vẫn còn các lịch hẹn ở trạng thái Chờ xác nhận hoặc Chờ thực hiện.");

        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> branchService.createUpdate(branchDto));
        Assert.assertEquals("Không thể khóa chi nhánh vì vẫn còn các lịch hẹn ở trạng thái Chờ xác nhận hoặc Chờ thực hiện.", ssdsBusinessException.getMessage());
    }

    @Test
    public void when_branchNotExist_should_updateThrowException() {
        BranchDto branchDto = initBranchDTO();
        branchDto.setId(1L);

        Mockito.when(branchRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> branchService.createUpdate(branchDto));
        Assert.assertEquals("BRANCH_NOT_EXIST", ssdsBusinessException.getCode());
    }

    private BranchDto initBranchDTO() {
        BranchDto branchDto = new BranchDto();
        branchDto.setName("Skin Wisdom Yên Lãng");
        branchDto.setCode("SKIN_WISDOM_YEN_LANG");
        branchDto.setDetailAddress("15 P.Yên Lãng");
        branchDto.setLatitude(21.0132533);
        branchDto.setLongitude(105.8185914);
        branchDto.setHotline("0363769123");

        LocationDTO state = new LocationDTO();
        state.setId(18L);
        branchDto.setState(state);

        LocationDTO city = new LocationDTO();
        city.setId(256L);
        branchDto.setCity(city);

        LocationDTO district = new LocationDTO();
        district.setId(1200L);
        branchDto.setDistrict(district);
        return branchDto;
    }

    private Branch initBranch() {
        Branch branch = new Branch();
        branch.setName("Skin Wisdom Yên Lãng");
        branch.setCode("SKIN_WISDOM_YEN_LANG");
        branch.setDetailAddress("15 P.Yên Lãng");
        branch.setLatitude(21.0132533);
        branch.setLongitude(105.8185914);
        branch.setHotline("0363769123");

        Location state = new Location();
        state.setId(18L);
        state.setDivisionName("Hà Nội");
        branch.setState(state);

        Location city = new Location();
        city.setId(256L);
        city.setDivisionName("Quận Đống Đa");
        branch.setCity(city);

        Location district = new Location();
        district.setId(1200L);
        district.setDivisionName("Phường Phương Liệt");
        branch.setDistrict(district);
        return branch;
    }

    private Location initDistrict() {
        Location district = new Location();
        district.setId(1200L);
        district.setDivisionName("Phường Phương Liệt");
        district.setDivisionCode("HA_NOI_QUAN_THANH_XUAN_PHUONG_PHUONG_LIET");
        district.setDivisionLevel("DISTRICT");
        district.setDivisionParentId(254L);
        return district;
    }

    private List<Location> initListLocation() {
        List<Location> locations = new ArrayList<>();
        Location state = new Location();
        state.setId(18L);
        state.setDivisionName("Hà Nội");
        locations.add(state);

        Location city = new Location();
        city.setId(256L);
        city.setDivisionName("Quận Đống Đa");
        locations.add(city);

        Location district = new Location();
        district.setId(1200L);
        district.setDivisionName("Phường Phương Liệt");
        locations.add(district);
        return locations;
    }
}
