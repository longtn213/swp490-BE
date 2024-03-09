package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.domain.AppointmentTracking;
import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.domain.ConfigData;
import com.fpt.ssds.repository.AppointmentMasterRepository;
import com.fpt.ssds.repository.AppointmentTrackingRepository;
import com.fpt.ssds.repository.ConfigDataRepository;
import com.fpt.ssds.service.ConfigDataService;
import com.fpt.ssds.service.dto.ConfigDataDTO;
import com.fpt.ssds.service.dto.WarningResponseDTO;
import com.fpt.ssds.service.mapper.ConfigDataMapper;
import com.fpt.ssds.utils.DateUtils;
import com.fpt.ssds.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigDataServiceImpl implements ConfigDataService {
    private final Logger log = LoggerFactory.getLogger(ConfigDataServiceImpl.class);

    private final ConfigDataRepository configDataRepository;

    private final ConfigDataMapper configDataMapper;

    private final MessageSource messageSource;

    private final AppointmentTrackingRepository appointmentTrackingRepository;

    private final AppointmentMasterRepository appointmentMasterRepository;


    @Value("${ssds.config.timezone}")
    String systemTimezone;

    @Override
    public Integer getIntegerByKey(Long bracnchId, String key, Integer defaultValue) {
        ConfigData configData = configDataRepository.findByConfigKeyAndBranch(key, bracnchId).orElse(null);
        if (Objects.isNull(configData)) {
            return defaultValue;
        }
        Integer valueReturn = null;
        String value = configData.getConfigValue();
        try {
            valueReturn = StringUtils.isEmpty(value) ? defaultValue : Integer.valueOf(value);
        } catch (Exception e) {

        }
        if (Objects.isNull(valueReturn)) {
            valueReturn = defaultValue;
        }
        return valueReturn;
    }

    @Override
    public Boolean getBooleanByKey(Long bracnchId, String key, Boolean defaultValue) {
        ConfigData configData = configDataRepository.findByConfigKeyAndBranch(key, bracnchId).orElse(null);
        if (Objects.isNull(configData)) {
            return defaultValue;
        }
        Boolean valueReturn = null;
        String value = configData.getConfigValue();
        try {
            if ("true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
                valueReturn = Boolean.TRUE;
            } else if ("false".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
                valueReturn = Boolean.FALSE;
            }
        } catch (Exception e) {

        }
        return Objects.isNull(valueReturn) ? defaultValue : valueReturn;
    }

    @Override
    public String getStringByKey(Long bracnchId, String key, String defaultValue) {
        ConfigData configData = configDataRepository.findByConfigKeyAndBranch(key, bracnchId).orElse(null);
        if (Objects.isNull(configData)) {
            return defaultValue;
        }
        return Objects.isNull(configData.getConfigValue()) ? defaultValue : configData.getConfigValue();
    }


    @Override
    @Transactional
    public ConfigDataDTO getById(Long id) {
        return configDataMapper.toDto(findById(id));
    }

    @Override
    public Integer getIntegerByKey(String key, Integer defaultValue) {
        ConfigData configData = configDataRepository.findByConfigKey(key).orElse(null);
        if (Objects.isNull(configData)) {
            return defaultValue;
        }
        Integer valueReturn = null;
        String value = configData.getConfigValue();
        try {
            valueReturn = StringUtils.isEmpty(value) ? defaultValue : Integer.valueOf(value);
        } catch (Exception e) {

        }
        return valueReturn;
    }

    @Override
    public void createConfigForNewBranch(Branch branch) {
        List<ConfigData> listConfig = configDataRepository.findAllByBranchCode(Constants.BRANCH.SKIN_WISDOM_YEN_LANG);
        if (CollectionUtils.isNotEmpty(listConfig)) {
            List<ConfigDataDTO> newConfigs = configDataMapper.toDto(listConfig);
            for (ConfigDataDTO newConfig : newConfigs) {
                newConfig.setId(null);
                newConfig.setBranchId(branch.getId());
            }
            configDataRepository.saveAll(configDataMapper.toEntity(newConfigs));
        }
    }

    @Override
    @Transactional
    public WarningResponseDTO updateConfig(ConfigDataDTO configDataDTO) {
        WarningResponseDTO warningResponseDTO = new WarningResponseDTO();
        validateRequiredDataBeforeUpdate(configDataDTO);
        ConfigData configData = findById(configDataDTO.getId());

        if (Objects.nonNull(configData.getType())) {
            switch (configData.getType()) {
                case Constants.CONFIG_TYPE.TIME:
                    warningResponseDTO = updateTimeConfig(configData, configDataDTO);
                    break;
                case Constants.CONFIG_TYPE.NUMBER:
                    warningResponseDTO = updateNumberConfig(configData, configDataDTO);
                    break;
                default:
                    configData.setConfigValue(configDataDTO.getConfigValue());
                    configDataRepository.save(configData);
                    break;
            }
        }
        return warningResponseDTO;
    }

    private WarningResponseDTO updateNumberConfig(ConfigData configData, ConfigDataDTO configDataDTO) {
        WarningResponseDTO warningResponseDTO = new WarningResponseDTO();
        if (!NumberUtils.isParsable(configDataDTO.getConfigValue())) {
            String message = messageSource.getMessage("config.data.config.value.must.be.a.number", null, null);
            message = new MessageFormat(message).format(Arrays.asList(configData.getConfigKey()).toArray());
            throw new SSDSBusinessException(null, message);
        }
        configData.setConfigValue(configDataDTO.getConfigValue());
        configDataRepository.save(configData);

        Long branchId = configData.getBranch().getId();
        if (Constants.CONFIG_KEY.MAX_CUSTOMER_A_TIME.equals(configData.getConfigKey())) {
            if (!configDataDTO.getAutoByPassWarning()) {
                Integer maxCustomer = getIntegerByKey(branchId, Constants.CONFIG_KEY.MAX_CUSTOMER_A_TIME, 10);
                List<AppointmentTracking> invalidTrackings = appointmentTrackingRepository.findByBranchIdAndTimeGreaterThanAndAndBookedQtyGreaterThan(branchId, Instant.now(), Long.valueOf(maxCustomer));
                if (CollectionUtils.isNotEmpty(invalidTrackings)) {
                    List<Instant> invalidTime = invalidTrackings.stream().map(AppointmentTracking::getTime).collect(Collectors.toList());
                    String message = messageSource.getMessage("warning.after.change.max.customer.qty", Arrays.asList(configDataDTO.getConfigValue()).toArray(), null);
                    throw new SSDSBusinessException(null, message);
                }
            }
            updateAppointmentTrackingAfterUpdateConfig(branchId);
        }

        return new WarningResponseDTO();
    }

    private WarningResponseDTO updateTimeConfig(ConfigData configData, ConfigDataDTO configDataDTO) {
        WarningResponseDTO warningResponseDTO = new WarningResponseDTO();
        List<Instant> timeHaveAppointment = new ArrayList<>();
        Long branchId = configData.getBranch().getId();

        if (!configDataDTO.getConfigValue().matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$")) {
            throw new SSDSBusinessException(ErrorConstants.INVALID_TIME_FORMAT);
        }

        if (Constants.CONFIG_KEY.START_WORKING_TIME_IN_DAY.equals(configData.getConfigKey()) || Constants.CONFIG_KEY.END_WORKING_TIME_IN_DAY.equals(configData.getConfigKey())) {
            validateWorkingTime(configData, configDataDTO, branchId, timeHaveAppointment);
        }

        configData.setConfigValue(configDataDTO.getConfigValue());
        configDataRepository.save(configData);
        if (CollectionUtils.isNotEmpty(timeHaveAppointment)) {
            List<AppointmentMaster> appointmentMasters = appointmentMasterRepository.findByExpectedStartTimeInAndStatusCodeNotInAndAndBranchId(timeHaveAppointment, Arrays.asList(Constants.APPOINTMENT_MASTER_STATUS.CLOSED, Constants.APPOINTMENT_MASTER_STATUS.CANCELED, Constants.APPOINTMENT_MASTER_STATUS.COMPLETED), branchId);
            if (CollectionUtils.isNotEmpty(appointmentMasters)) {
                List<Long> appointmentId = appointmentMasters.stream().map(AppointmentMaster::getId).collect(Collectors.toList());
                String message = messageSource.getMessage("warning.after.change.working.time", Arrays.asList(StringUtils.join(appointmentId, ", ")).toArray(), null);
                warningResponseDTO.setWarningMessage(message);
            }
        }
        if (Arrays.asList(Constants.CONFIG_KEY.START_WORKING_TIME_IN_DAY, Constants.CONFIG_KEY.END_WORKING_TIME_IN_DAY).contains(configData.getConfigKey())) {
            updateAppointmentTrackingAfterUpdateConfig(branchId);
        }

        return warningResponseDTO;
    }


    private void validateWorkingTime(ConfigData configData, ConfigDataDTO configDataDTO, Long branchId, List<Instant> timeHaveAppointment) {
        int startTimeToSecond = 0;
        int endTimeToSecond = 0;

        if (Constants.CONFIG_KEY.START_WORKING_TIME_IN_DAY.equals(configData.getConfigKey())) {
            startTimeToSecond = Utils.getSecondFromString(configDataDTO.getConfigValue());
            String endTimeInDay = getStringByKey(branchId, Constants.CONFIG_KEY.END_WORKING_TIME_IN_DAY, "21:30:00");
            endTimeToSecond = Utils.getSecondFromString(endTimeInDay);
        } else {
            endTimeToSecond = Utils.getSecondFromString(configDataDTO.getConfigValue());
            String startTimeInDay = getStringByKey(branchId, Constants.CONFIG_KEY.START_WORKING_TIME_IN_DAY, "09:30:00");
            startTimeToSecond = Utils.getSecondFromString(startTimeInDay);
        }
        if (startTimeToSecond > endTimeToSecond) {
            throw new SSDSBusinessException(null, "Giờ bắt đầu làm việc phải trước giờ đóng cửa.");
        }

        List<AppointmentTracking> appointmentTrackings = appointmentTrackingRepository.findByBranchIdAndTimeGreaterThanAndAndBookedQtyGreaterThan(branchId, Instant.now(), Long.valueOf(0));
        for (AppointmentTracking appointmentTracking : appointmentTrackings) {
            LocalDateTime time = DateUtils.dateToLocalDateTime(appointmentTracking.getTime(), systemTimezone);
            int second = (time.getHour() * 60 + time.getMinute()) * 60 + time.getSecond();
            if (second < startTimeToSecond || second > endTimeToSecond) {
                timeHaveAppointment.add(appointmentTracking.getTime());
            }
        }
    }

    private ConfigData findById(Long id) {
        Optional<ConfigData> configOpt = configDataRepository.findById(id);
        if (configOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.CONFIG_DATA_NOT_EXIST);
        }
        return configOpt.get();
    }

    private void validateRequiredDataBeforeUpdate(ConfigDataDTO configDataDTO) {
        if (Objects.isNull(configDataDTO.getId())) {
            throw new SSDSBusinessException(null, messageSource.getMessage("config.data.id.required", null, null));
        }
        if (Objects.isNull(configDataDTO.getConfigValue())) {
            throw new SSDSBusinessException(null, messageSource.getMessage("config.data.config.value.required", null, null));
        }
    }

    private void updateAppointmentTrackingAfterUpdateConfig(Long branchId) {
        List<AppointmentTracking> lstAppointmenTracking = appointmentTrackingRepository.findByBranchIdAndTimeGreaterThanOrderByTime(branchId, Instant.now());
        Instant recordTime = lstAppointmenTracking.get(0).getTime();

        String startTimeInDay = getStringByKey(branchId, Constants.CONFIG_KEY.START_WORKING_TIME_IN_DAY, "09:30:00");
        String endTimeInDay = getStringByKey(branchId, Constants.CONFIG_KEY.END_WORKING_TIME_IN_DAY, "21:30:00");
        Integer maxCustomerADay = getIntegerByKey(branchId, Constants.CONFIG_KEY.MAX_CUSTOMER_A_TIME, 10);
        int startTimeInDayToSec = Utils.getSecondFromString(startTimeInDay);
        int endTimeInDayToSec = Utils.getSecondFromString(endTimeInDay);
        Instant startWorkingTime = DateUtils.getInstantByDayAndSecond(recordTime, startTimeInDayToSec, systemTimezone);
        Instant endWorkingTime = DateUtils.getInstantByDayAndSecond(recordTime, endTimeInDayToSec, systemTimezone);

        for (int i = 0; i < lstAppointmenTracking.size(); i++) {
            AppointmentTracking appointmentTracking = lstAppointmenTracking.get(i);
            appointmentTracking.setIsFirstTimeInDay(startWorkingTime.equals(recordTime) ? true : false);
            appointmentTracking.setIsLastTimeInDay(endWorkingTime.equals(recordTime) ? true : false);
            appointmentTracking.setMaxQty(Long.valueOf(maxCustomerADay));
            if (!recordTime.isBefore(startWorkingTime) && !recordTime.isAfter(endWorkingTime) && appointmentTracking.getBookedQty() < appointmentTracking.getMaxQty()) {
                appointmentTracking.setIsAvailable(true);
            } else {
                appointmentTracking.setIsAvailable(false);
            }
            if (i + 1 < lstAppointmenTracking.size()) {
                recordTime = lstAppointmenTracking.get(i + 1).getTime();
                if (recordTime.equals(startWorkingTime.plus(1, ChronoUnit.DAYS))) {
                    startWorkingTime = startWorkingTime.plus(1, ChronoUnit.DAYS);
                    endWorkingTime = endWorkingTime.plus(1, ChronoUnit.DAYS);
                }
            }
        }
        appointmentTrackingRepository.saveAll(lstAppointmenTracking);
    }
}
