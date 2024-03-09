package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.domain.ConfigData;
import com.fpt.ssds.repository.ConfigDataRepository;
import com.fpt.ssds.service.dto.ConfigDataDTO;
import com.fpt.ssds.service.mapper.BranchMapper;
import com.fpt.ssds.service.mapper.ConfigDataMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ConfigDataServiceImplTest {
    @InjectMocks
    ConfigDataServiceImpl configDataService;

    @Mock
    ConfigDataRepository configDataRepository;

    @Spy
    private ConfigDataMapper configDataMapper = Mappers.getMapper(ConfigDataMapper.class);

    @Spy
    private BranchMapper branchMapper = Mappers.getMapper(BranchMapper.class);

    @Captor
    ArgumentCaptor<List<ConfigData>> configsCaptor;

    @Mock
    MessageSource messageSource;

    @Before
    public void before() {
        ReflectionTestUtils.setField(configDataMapper, "branchMapper", branchMapper);
    }

    @Test
    public void when_configValueIsNotInteger_should_getIntegerByKeyReturnDefaultValue() {
        ConfigData configData = initStringConfig();

        Integer defaultValue = 0;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Integer returnedValue = configDataService.getIntegerByKey(1L, "STRING_CONFIG", defaultValue);
        Assert.assertEquals(returnedValue, defaultValue);
    }

    @Test
    public void when_configValueIsInteger_should_getIntegerByKeyReturnConfigValue() {
        ConfigData configData = initIntegerConfig();

        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Integer returnedValue = configDataService.getIntegerByKey(1L, "INTEGER_CONFIG", 0);
        Assert.assertEquals(returnedValue, Integer.valueOf(10));
    }

    @Test
    public void when_configValueIsNull_should_getIntegerByKeyReturnDefaultValue() {
        ConfigData configData = initIntegerConfig();
        configData.setConfigValue(null);

        Integer defaultValue = 0;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Integer returnedValue = configDataService.getIntegerByKey(1L, "INTEGER_CONFIG", defaultValue);
        Assert.assertEquals(returnedValue, defaultValue);
    }

    @Test
    public void when_configIsNull_should_getIntegerByKeyReturnDefaultValue() {
        Integer defaultValue = 0;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        Integer returnedValue = configDataService.getIntegerByKey(1L, "INTEGER_CONFIG", defaultValue);
        Assert.assertEquals(returnedValue, defaultValue);
    }

    @Test
    public void when_configValueIsString_should_getStringByKeyReturnConfigValue() {
        ConfigData configData = initStringConfig();

        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        String returnedValue = configDataService.getStringByKey(1L, "STRING_CONFIG", "D");
        Assert.assertEquals(returnedValue, "String value");
    }

    @Test
    public void when_configValueIsNull_should_getStringByKeyReturnDefaultValue() {
        ConfigData configData = initStringConfig();
        configData.setConfigValue(null);

        String defaultValue = "default string";
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        String returnedValue = configDataService.getStringByKey(1L, "STRING_CONFIG", defaultValue);
        Assert.assertEquals(returnedValue, defaultValue);
    }

    @Test
    public void when_configIsNull_should_getStringByKeyReturnDefaultValue() {
        String defaultValue = "default string";
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        String returnedValue = configDataService.getStringByKey(1L, "STRING_CONFIG", defaultValue);
        Assert.assertEquals(returnedValue, defaultValue);
    }

    @Test
    public void when_configValueIsNotABoolean_should_getBooleanByKeyReturnDefaultValue() {
        ConfigData configData = initStringConfig();

        Boolean defaultValue = true;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Boolean returnedValue = configDataService.getBooleanByKey(1L, "BOOLEAN_CONFIG", defaultValue);
        Assert.assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void when_configValueIsBoolean_should_getBooleanByKeyReturnConfigValue() {
        ConfigData configData = initBooleanConfig();

        Boolean defaultValue = false;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Boolean returnedValue = configDataService.getBooleanByKey(1L, "BOOLEAN_CONFIG", defaultValue);
        Assert.assertEquals(Boolean.TRUE, returnedValue);
    }

    @Test
    public void when_configValueIsNull_should_getBooleanByKeyReturnDefaultValue() {
        ConfigData configData = initBooleanConfig();
        configData.setConfigValue(null);

        Boolean defaultValue = false;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Boolean returnedValue = configDataService.getBooleanByKey(1L, "BOOLEAN_CONFIG", defaultValue);
        Assert.assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void when_configIsNull_should_getBooleanByKeyReturnDefaultValue() {
        Boolean defaultValue = false;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        Boolean returnedValue = configDataService.getBooleanByKey(1L, "BOOLEAN_CONFIG", defaultValue);
        Assert.assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void when_configIsTrue_should_getBooleanByKeyReturnTrue() {
        ConfigData configData = initBooleanConfig();

        Boolean defaultValue = false;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Boolean returnedValue = configDataService.getBooleanByKey(1L, "BOOLEAN_CONFIG", defaultValue);
        Assert.assertEquals(Boolean.TRUE, returnedValue);
    }

    @Test
    public void when_configIs1_should_getBooleanByKeyReturnTrue() {
        ConfigData configData = initBooleanConfig();
        configData.setConfigValue("1");

        Boolean defaultValue = false;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Boolean returnedValue = configDataService.getBooleanByKey(1L, "BOOLEAN_CONFIG", defaultValue);
        Assert.assertEquals(Boolean.TRUE, returnedValue);
    }

    @Test
    public void when_configIsFalse_should_getBooleanByKeyReturnFalse() {
        ConfigData configData = initBooleanConfig();
        configData.setConfigValue("false");

        Boolean defaultValue = false;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Boolean returnedValue = configDataService.getBooleanByKey(1L, "BOOLEAN_CONFIG", defaultValue);
        Assert.assertEquals(Boolean.FALSE, returnedValue);
    }

    @Test
    public void when_configIs0_should_getBooleanByKeyReturnFalse() {
        ConfigData configData = initBooleanConfig();
        configData.setConfigValue("0");

        Boolean defaultValue = false;
        Mockito.when(configDataRepository.findByConfigKeyAndBranch(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(configData));
        Boolean returnedValue = configDataService.getBooleanByKey(1L, "BOOLEAN_CONFIG", defaultValue);
        Assert.assertEquals(Boolean.FALSE, returnedValue);
    }

    @Test
    public void when_configNotExist_should_getByIdthrowException() {
        Mockito.when(configDataRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> configDataService.getById(1L));
        Assert.assertEquals("CONFIG_DATA_NOT_EXIST", ssdsBusinessException.getCode());
    }

    @Test
    public void when_configExist_should_returnConfigDto() {
        ConfigData configData = initStringConfig();
        Mockito.when(configDataRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(configData));
        ConfigDataDTO configDataDTO = configDataService.getById(1L);
        Assert.assertEquals(configData.getConfigKey(), configDataDTO.getConfigKey());
        Assert.assertEquals(configData.getConfigValue(), configDataDTO.getConfigValue());
        Assert.assertEquals(configData.getConfigDesc(), configDataDTO.getConfigDesc());
    }

    @Test
    public void when_createConfigForNewBranch_should_successfull() {
        List<ConfigData> configs = Arrays.asList(initStringConfig(), initIntegerConfig());
        Mockito.when(configDataRepository.findAllByBranchCode(Mockito.anyString())).thenReturn(configs);
        configDataService.createConfigForNewBranch(initBranch());

        Mockito.verify(configDataRepository).saveAll(configsCaptor.capture());
        List<ConfigData> savedConfigData = configsCaptor.getValue();
        Assert.assertEquals(2, savedConfigData.size());
        Assert.assertEquals("STRING_CONFIG", savedConfigData.get(0).getConfigKey());
        Assert.assertEquals("INTEGER_CONFIG", savedConfigData.get(1).getConfigKey());
        Assert.assertEquals(Long.valueOf(1), savedConfigData.get(0).getBranch().getId());
        Assert.assertEquals(Long.valueOf(1), savedConfigData.get(1).getBranch().getId());
    }

    @Test
    public void when_branchIdIsNot_should_updateConfigThrowException() {
        ConfigDataDTO configDataDTO = new ConfigDataDTO();

        Mockito.when(messageSource.getMessage("config.data.id.required", null, null)).thenReturn("Vui lòng không để trống mã cấu hình");
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> configDataService.updateConfig(configDataDTO));
        Assert.assertEquals("Vui lòng không để trống mã cấu hình", ssdsBusinessException.getMessage());
    }

    @Test
    public void when_configValueIsNot_should_updateConfigThrowException() {
        ConfigDataDTO configDataDTO = new ConfigDataDTO();
        configDataDTO.setBranchId(1L);
        configDataDTO.setId(1L);

        Mockito.when(messageSource.getMessage("config.data.config.value.required", null, null)).thenReturn("Vui lòng không để trống giá trị cấu hình");
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> configDataService.updateConfig(configDataDTO));
        Assert.assertEquals("Vui lòng không để trống giá trị cấu hình", ssdsBusinessException.getMessage());
    }

    @Test
    public void when_updateTimeConfigWithInvalidFormat_should_updateConfigThrowException() {
        ConfigDataDTO configDataDTO = new ConfigDataDTO();
        configDataDTO.setId(1L);
        configDataDTO.setConfigValue("invalid");

        ConfigData configData = new ConfigData();
        configData.setType(Constants.CONFIG_TYPE.TIME);
        Branch branch = new Branch();
        branch.setId(1L);
        configData.setBranch(branch);

        Mockito.when(configDataRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(configData));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> configDataService.updateConfig(configDataDTO));
        Assert.assertEquals("INVALID_TIME_FORMAT", ssdsBusinessException.getCode());
    }

    @Test
    public void when_startWorkingTimeIsAfterEndWorkingTimeInDay_should_updateConfigThrowException() {
        ConfigDataDTO configDataDTO = new ConfigDataDTO();
        configDataDTO.setId(1L);
        configDataDTO.setConfigValue("22:00:00");

        ConfigData startTimeConfig = initTimeConfig(Constants.CONFIG_KEY.START_WORKING_TIME_IN_DAY, "22:00:00");

        Mockito.when(configDataRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(startTimeConfig));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> configDataService.updateConfig(configDataDTO));
        Assert.assertEquals("Giờ bắt đầu làm việc phải trước giờ đóng cửa.", ssdsBusinessException.getMessage());
    }

    @Test
    public void when_configTypeIsNumberAndConfigValueIsNaN_should_updateConfigThrowException() {
        ConfigDataDTO configDataDTO = new ConfigDataDTO();
        configDataDTO.setId(1L);
        configDataDTO.setConfigValue("Something");

        ConfigData startTimeConfig = initIntegerConfig();

        Mockito.when(configDataRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(startTimeConfig));
        Mockito.when(messageSource.getMessage("config.data.config.value.must.be.a.number", null, null)).thenReturn("Giá trị cấu hình {0} phải ở dạng số.");
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> configDataService.updateConfig(configDataDTO));
        Assert.assertEquals("Giá trị cấu hình INTEGER_CONFIG phải ở dạng số.", ssdsBusinessException.getMessage());
    }

    @Test
    public void when_requestIsValid_should_updateConfigThrowException() {
        ConfigDataDTO configDataDTO = new ConfigDataDTO();
        configDataDTO.setId(1L);
        configDataDTO.setConfigValue("Something");

        ConfigData configData = initStringConfig();

        Mockito.when(configDataRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(configData));
        configDataService.updateConfig(configDataDTO);
        Mockito.verify(configDataRepository, Mockito.times(1)).save(Mockito.any());
    }

    private ConfigData initIntegerConfig() {
        Branch branch = initBranch();

        ConfigData configData = new ConfigData();
        configData.setId(1L);
        configData.setBranch(branch);
        configData.setConfigKey("INTEGER_CONFIG");
        configData.setConfigValue("10");
        configData.setType(Constants.CONFIG_TYPE.NUMBER);
        return configData;
    }

    private ConfigData initStringConfig() {
        Branch branch = initBranch();

        ConfigData configData = new ConfigData();
        configData.setId(1L);
        configData.setBranch(branch);
        configData.setConfigKey("STRING_CONFIG");
        configData.setConfigValue("String value");
        configData.setType(Constants.CONFIG_TYPE.TEXT);
        return configData;
    }

    private ConfigData initBooleanConfig() {
        Branch branch = initBranch();

        ConfigData configData = new ConfigData();
        configData.setId(1L);
        configData.setBranch(branch);
        configData.setConfigKey("BOOLEAN_CONFIG");
        configData.setConfigValue("true");
        return configData;
    }

    private Branch initBranch() {
        Branch branch = new Branch();
        branch.setId(1L);
        return branch;
    }

    private ConfigData initTimeConfig(String key, String value) {
        Branch branch = initBranch();

        ConfigData configData = new ConfigData();
        configData.setId(1L);
        configData.setBranch(branch);
        configData.setConfigKey(key);
        configData.setConfigValue(value);
        configData.setType(Constants.CONFIG_TYPE.TIME);
        return configData;
    }
}
