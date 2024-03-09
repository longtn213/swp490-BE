package com.fpt.ssds.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.ssds.domain.AppointmentTracking;
import com.fpt.ssds.domain.SpaService;
import com.fpt.ssds.repository.AppointmentTrackingRepository;
import com.fpt.ssds.repository.SpaServiceRepository;
import com.fpt.ssds.service.mapper.AppointmentTrackingMapper;

import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentTrackingServiceImplTest {
    @InjectMocks
    AppointmentTrackingServiceImpl appointmentTrackingService;

    @Mock
    AppointmentTrackingRepository appointmentTrackingRepository;

    @Mock
    SpaServiceRepository spaServiceRepository;

    @Spy
    private AppointmentTrackingMapper appointmentTrackingMapper = Mappers.getMapper(AppointmentTrackingMapper.class);

    /*@Test
    public void initAppointmentTracking() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setBranchCode("BRANCH_CODE");
        requestDto.setListServicesId(Arrays.asList(1L));

        Mockito.when(appointmentTrackingRepository.findByTimeGreaterThan(Mockito.any())).thenReturn(initApptTrackingData());
        Mockito.when(spaServiceRepository.findAllById(Mockito.any())).thenReturn(initServices());

        List<String> availableTimeByBranch = appointmentTrackingService.getAvailableTimeByBranch(requestDto);
        System.out.println("");
    }*/

    private List<AppointmentTracking> initApptTrackingData() {
        InputStream inJson = AppointmentTracking[].class.getResourceAsStream("/json/appointment-tracking.json");
        AppointmentTracking[] apptTrackings = new AppointmentTracking[0];
        try {
            apptTrackings = new ObjectMapper().readValue(inJson, AppointmentTracking[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.asList(apptTrackings);
    }

    private List<SpaService> initServices() {
        List<SpaService> services = new ArrayList<>();

        SpaService spaService = new SpaService();
        spaService.setId(1L);
        spaService.setDuration(60L);
        services.add(spaService);

        return services;
    }
}
