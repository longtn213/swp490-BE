package com.fpt.ssds.web.rest;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.domain.User;
import com.fpt.ssds.domain.enumeration.AppointmentMasterAction;
import com.fpt.ssds.service.AppointmentMasterService;
import com.fpt.ssds.service.dto.AppointmentMasterDto;
import com.fpt.ssds.service.dto.ConfirmAppointmentRequestDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.criteria.AppointmentMasterCriteria;
import com.fpt.ssds.service.queryservice.AppointmentMasterServiceQuery;
import com.fpt.ssds.utils.HTTPUtils;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.service.filter.StringFilter;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static com.fpt.ssds.constant.Constants.COMMON.USER;
import static com.fpt.ssds.utils.ResponseUtils.CODE_OK;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/appointment-master")
@RequiredArgsConstructor
public class AppointmentMasterController {
    private final AppointmentMasterService appointmentMasterService;
    private final AppointmentMasterServiceQuery appointmentMasterServiceQuery;

    private final MessageSource messageSource;

    @PostMapping("")
    public ResponseEntity<ResponseDTO> createUpdate(@RequestBody @Valid AppointmentMasterDto appointmentMasterDto) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(appointmentMasterService.createUpdate(appointmentMasterDto)));
    }

    @GetMapping("/{apptMasterId}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable("apptMasterId") Long apptMasterId,
                                               HttpServletRequest httpServletRequest) {
        User user = (User) HTTPUtils.getAttribute(httpServletRequest, USER);
        AppointmentMasterDto apptMasterDto = appointmentMasterService.getById(apptMasterId, user);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(apptMasterDto));
    }

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getAll(AppointmentMasterCriteria criteria, Pageable pageable, HttpServletRequest httpServletRequest) {
        User user = (User) HTTPUtils.getAttribute(httpServletRequest, USER);
        if (Objects.nonNull(user.getBranch())) {
            StringFilter branchCodeFilter = new StringFilter();
            branchCodeFilter.setEquals(user.getBranch().getCode());

            criteria.setBranchCode(branchCodeFilter);
        }
        Page<AppointmentMasterDto> page = appointmentMasterServiceQuery.findByCriteria(criteria, pageable);
        ResponseDTO response = ResponseUtils.responseOK(page.getContent());
        response.getMeta().setTotal(page.getTotalElements());
        response.getMeta().setPage(page.getNumber());
        response.getMeta().setSize(page.getSize());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/confirm-appointment")
    public ResponseEntity<ResponseDTO> confirmAppointments(@RequestBody @Valid List<ConfirmAppointmentRequestDto> confirmAppointmentRequestDtos) {
        appointmentMasterService.confirmAppointments(confirmAppointmentRequestDtos);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }

    @PostMapping("/cancel")
    public ResponseEntity<ResponseDTO> cancelAppointmentMaster(@RequestBody List<AppointmentMasterDto> appointmentMasterDtos) {
        appointmentMasterService.cancelAppointmentMaster(appointmentMasterDtos);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }

    @PostMapping("{appt_master_id}/{action}")
    @Transactional
    public ResponseEntity<ResponseDTO> updateAppointmentByAction(HttpServletRequest request,
                                                                 @PathVariable(name = "appt_master_id") long taskGroupId,
                                                                 @PathVariable(name = "action") String action,
                                                                 @RequestBody AppointmentMasterDto appointmentMasterDto) {
        ResponseDTO response = null;

        // validate input
        AppointmentMaster appointmentMaster = appointmentMasterService.findById(taskGroupId);

        if (!AppointmentMasterAction.isExisted(action)) {
            throw new SSDSBusinessException(null, messageSource.getMessage("appointment.master.action.invalid", null, null));
        }

        AppointmentMasterAction amAction = AppointmentMasterAction.getAppointmentMasterAction(action);
        switch (amAction) {
            case CHECKIN:
                response = appointmentMasterService.checkin(appointmentMaster, appointmentMasterDto);
                break;
            case CHECKOUT:
                response = appointmentMasterService.checkout(appointmentMaster, appointmentMasterDto);
                break;
        }
        if (Objects.nonNull(response) && Objects.nonNull(response.getMeta()) && !CODE_OK.equals(response.getMeta().getCode())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    /*@PostMapping("")
    public ResponseEntity<ResponseDTO> addExtraApptService(@RequestBody AppointmentMasterDto appointmentMasterDto) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(appointmentMasterService.addExtraApptService(appointmentMasterDto)));
    }*/
}
