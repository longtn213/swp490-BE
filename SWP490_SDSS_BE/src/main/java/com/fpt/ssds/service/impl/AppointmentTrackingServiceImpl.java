package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.*;
import com.fpt.ssds.repository.AppointmentTrackingRepository;
import com.fpt.ssds.repository.BranchRepository;
import com.fpt.ssds.repository.SpaServiceRepository;
import com.fpt.ssds.service.AppointmentTrackingService;
import com.fpt.ssds.service.ConfigDataService;
import com.fpt.ssds.service.dto.AppointmentTrackingDto;
import com.fpt.ssds.service.dto.BookingRequestDto;
import com.fpt.ssds.service.JobExecutionParamsService;
import com.fpt.ssds.service.mapper.AppointmentTrackingMapper;
import com.fpt.ssds.utils.DateUtils;
import com.fpt.ssds.utils.Utils;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentTrackingServiceImpl implements AppointmentTrackingService {
    private final ConfigDataService configDataService;

    private final MessageSource messageSource;

    private final SpaServiceRepository spaServiceRepository;

    private final AppointmentTrackingRepository appointmentTrackingRepository;

    private final JobExecutionParamsService jobExecutionParamsService;
    private final BranchRepository branchRepository;

    private final AppointmentTrackingMapper appointmentTrackingMapper;

    @Value("${ssds.config.timezone}")
    String systemTimezone;

    @Override
    public void autoCreateData(Long branchId) {
        Integer maxCustomer = configDataService.getIntegerByKey(branchId, Constants.CONFIG_KEY.MAX_CUSTOMER_A_TIME, 10);
        Integer minutesBetweenAm = configDataService.getIntegerByKey(branchId, Constants.CONFIG_KEY.PERIOD_BETWEEN_APPOINTMENT, 15);
        String startTimeInDay = configDataService.getStringByKey(branchId, Constants.CONFIG_KEY.START_WORKING_TIME_IN_DAY, "09:30:00");
        String endTimeInDay = configDataService.getStringByKey(branchId, Constants.CONFIG_KEY.END_WORKING_TIME_IN_DAY, "21:30:00");
        Integer durationAutoGenData = configDataService.getIntegerByKey(Constants.CONFIG_KEY.DURATION_AUTO_GEN_APPOINTMENT_TRACKING_TIME, 90);

        Optional<Branch> branchOpt = branchRepository.findById(branchId);
        if (branchOpt.isPresent()) {
            int startTimeInDayToSec = Utils.getSecondFromString(startTimeInDay);
            int endTimeInDayToSec = Utils.getSecondFromString(endTimeInDay);

            JobExecutionParams jobExecutionParams = jobExecutionParamsService.findByParamKey(Constants.BATCH.JOB_APPOINTMENT_TRACKING + "_" + Constants.BATCH.NEXT_BEGIN_TIME + "_" + branchId);
            Instant recordTime = Instant.ofEpochMilli(Long.valueOf(jobExecutionParams.getParamValue()));
            if (recordTime.compareTo(Instant.now()) <= 0) {
                recordTime = DateUtils.getInstantByDayAndSecond(Instant.now(), startTimeInDayToSec, systemTimezone);
            }

            Instant startTime = DateUtils.atStartOfDay(Instant.now(), systemTimezone);
            Instant endTime = DateUtils.atStartOfDay(startTime.plus(durationAutoGenData + 1, ChronoUnit.DAYS), systemTimezone);
            if ((recordTime.isAfter(startTime))) {
                startTime = recordTime;
            }
            Instant startWorkingTime = DateUtils.getInstantByDayAndSecond(recordTime, startTimeInDayToSec, systemTimezone);
            Instant endWorkingTime = DateUtils.getInstantByDayAndSecond(recordTime, endTimeInDayToSec, systemTimezone);

            List<AppointmentTracking> appointmentTrackings = new ArrayList<>();
            log.debug("start insert data_tracking from time {}, branId {}", startTime, branchId);
            while (recordTime.isBefore(endTime)) {
                AppointmentTracking appointmentTracking = new AppointmentTracking();
                appointmentTracking.setIsFirstTimeInDay(startWorkingTime.equals(recordTime) ? true : false);
                appointmentTracking.setIsLastTimeInDay(endWorkingTime.equals(recordTime) ? true : false);
                appointmentTracking.setTime(recordTime);
                appointmentTracking.setBookedQty(0L);
                appointmentTracking.setMaxQty(Long.valueOf(maxCustomer));
                if (!recordTime.isBefore(startWorkingTime) && !recordTime.isAfter(endWorkingTime)) {
                    appointmentTracking.setIsAvailable(true);
                } else {
                    appointmentTracking.setIsAvailable(false);
                }
                appointmentTracking.setBranch(branchOpt.get());
                appointmentTrackings.add(appointmentTracking);

                recordTime = recordTime.plus(minutesBetweenAm, ChronoUnit.MINUTES);
                if (recordTime.equals(startWorkingTime.plus(1, ChronoUnit.DAYS))) {
                    startWorkingTime = startWorkingTime.plus(1, ChronoUnit.DAYS);
                    endWorkingTime = endWorkingTime.plus(1, ChronoUnit.DAYS);
                }
            }

            jobExecutionParams.setParamValue(String.valueOf(recordTime.toEpochMilli()));
            jobExecutionParamsService.save(jobExecutionParams);
            appointmentTrackingRepository.saveAll(appointmentTrackings);
        }
    }

    @Override
    public List<Instant> getAvailableTimeByBranch(BookingRequestDto bookingRequestDto) {
        Instant now = Instant.now();
        validateGetAvailableTimeRequest(bookingRequestDto);
        Integer maxPeriodForBooking = configDataService.getIntegerByKey(bookingRequestDto.getBranchId(), Constants.CONFIG_KEY.MAX_PERIOD_BOOKING_TIME, 7);

        Instant endTime = DateUtils.atEndOfDay(now.plus(maxPeriodForBooking, ChronoUnit.DAYS), systemTimezone);
        List<AppointmentTracking> listAvailableTime = appointmentTrackingRepository.findByBranchIdAndTimeGreaterThanAndTimeLessThan(bookingRequestDto.getBranchId(), now, endTime);
        List<AppointmentTrackingDto> apptTrackingDtos = appointmentTrackingMapper.toDto(listAvailableTime);
        List<AppointmentTrackingDto> result = new ArrayList<>();

        List<SpaService> services = spaServiceRepository.findAllById(bookingRequestDto.getListServicesId());
        if (CollectionUtils.isNotEmpty(services)) {
            long totalTime = services.stream().filter(service -> Objects.nonNull(service.getDuration()))
                .mapToLong(SpaService::getDuration)
                .sum();

            List<AppointmentTrackingDto> notAvailApptTrackingDtos = apptTrackingDtos.stream()
                .filter(apptTracking -> !apptTracking.getIsAvailable() || apptTracking.getIsLastTimeInDay())
                .collect(Collectors.toList());


            RangeMap<Long, String> rangeMap = TreeRangeMap.create();
            if (CollectionUtils.isNotEmpty(notAvailApptTrackingDtos)) {
                for (int i = 0; i < notAvailApptTrackingDtos.size(); i++) {
                    AppointmentTrackingDto apptTrackingDto = notAvailApptTrackingDtos.get(i);
                    rangeMap.put(Range.closed(apptTrackingDto.getTime().minus(totalTime, ChronoUnit.MINUTES).toEpochMilli(), apptTrackingDto.getTime().toEpochMilli()), "NA");
                }
            }


            for (AppointmentTrackingDto dto : apptTrackingDtos) {
                long a = dto.getTime().toEpochMilli();
                if (Objects.isNull(rangeMap.get((Long) a))) {
                    result.add(dto);
                }
            }
        } else {
            result = apptTrackingDtos.stream().filter(AppointmentTrackingDto::getIsAvailable).collect(Collectors.toList());
        }

        return result.stream().map(dto -> dto.getTime()).sorted().collect(Collectors.toList());
        /*return result.stream()
            .sorted(Comparator.comparing(AppointmentTrackingDto::getTime))
            .map(dto -> DateUtils.formatInstantToString(dto.getTime(), systemTimezone, STR_DEFAULT_TIME_FORMAT))
            .collect(Collectors.toList());*/
    }

    private void validateGetAvailableTimeRequest(BookingRequestDto bookingRequestDto) {
        if (Objects.isNull(bookingRequestDto.getBranchCode())) {
            throw new SSDSBusinessException(messageSource.getMessage("branch.code.is.required", null, null));
        }
        Optional<Branch> branchOpt = branchRepository.findByCode(bookingRequestDto.getBranchCode());
        if (branchOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.BRANCH_NOT_EXIST, Arrays.asList(bookingRequestDto.getBranchCode()));
        }
        Branch branch = branchOpt.get();
        if (!branch.getIsActive()) {
            throw new SSDSBusinessException(null, messageSource.getMessage("branch.is.deactive", Arrays.asList(branch.getName()).toArray(), null));
        }
        bookingRequestDto.setBranchId(branch.getId());
    }

    @Override
    public void plusBookedQuantity(AppointmentMaster appointmentMaster) {
        List<AppointmentTracking> listApptTracking = appointmentTrackingRepository.findAllByTimeBetweenAndBranchId(appointmentMaster.getExpectedStartTime(), appointmentMaster.getExpectedEndTime(), appointmentMaster.getBranch().getId());
        for (AppointmentTracking apptTracking : listApptTracking) {
            apptTracking.setBookedQty(apptTracking.getBookedQty() + 1);
            if (apptTracking.getBookedQty().equals(apptTracking.getMaxQty())) {
                apptTracking.setIsAvailable(false);
            }
        }
        appointmentTrackingRepository.saveAll(listApptTracking);
    }

    @Override
    public void minusBookedQtyBetweenTime(Instant startTime, Instant endTime, Long branchId) {
        List<AppointmentTracking> appointmentTrackings = appointmentTrackingRepository.findAllByTimeBetweenAndBranchId(startTime, endTime, branchId);
        for (AppointmentTracking appointmentTracking : appointmentTrackings) {
            if (appointmentTracking.getBookedQty() > 0) {
                appointmentTracking.setBookedQty(appointmentTracking.getBookedQty() - 1);
            }
        }
        appointmentTrackingRepository.saveAll(appointmentTrackings);
    }
}
