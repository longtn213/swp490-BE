package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.domain.User;
import com.fpt.ssds.service.AppointmentTrackingService;
import com.fpt.ssds.service.ConfigDataService;
import com.fpt.ssds.service.criteria.ConfigDataCriteria;
import com.fpt.ssds.service.dto.ConfigDataDTO;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.queryservice.ConfigDataServiceQuery;
import com.fpt.ssds.utils.HTTPUtils;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.StringFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.fpt.ssds.constant.Constants.COMMON.USER;

@RestController("WebConfigDataController")
@RequestMapping("${ssds.api.ref.public}/web/v1/config")
@Slf4j
@RequiredArgsConstructor
public class ConfigDataController {
    private final ConfigDataServiceQuery configDataServiceQuery;

    private final ConfigDataService configDataService;

    private final AppointmentTrackingService appointmentTrackingService;

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getAllConfigData(ConfigDataCriteria criteria, Pageable pageable, HttpServletRequest httpServletRequest) {
        BooleanFilter allowUpdateFilter = new BooleanFilter();
        allowUpdateFilter.setEquals(true);
        criteria.setAllowUpdate(allowUpdateFilter);

        User user = (User) HTTPUtils.getAttribute(httpServletRequest, USER);
        if (Objects.nonNull(user)) {
            if (Objects.nonNull(user.getBranch())) {
                StringFilter branchCodeFilter = new StringFilter();
                branchCodeFilter.setEquals(user.getBranch().getCode());

                criteria.setBranchCode(branchCodeFilter);
            }
        }

        Page<ConfigDataDTO> page = configDataServiceQuery.findByCriteria(criteria, pageable);
        ResponseDTO response = ResponseUtils.responseOK(page.getContent());
        response.getMeta().setTotal(page.getTotalElements());
        response.getMeta().setPage(page.getNumber());
        response.getMeta().setSize(page.getSize());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
