package com.fpt.ssds.web.rest;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.User;
import com.fpt.ssds.service.UserService;
import com.fpt.ssds.service.criteria.UserCriteria;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.UserDto;
import com.fpt.ssds.service.dto.UserListingDTO;
import com.fpt.ssds.service.queryservice.UserQueryService;
import com.fpt.ssds.utils.HTTPUtils;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.service.filter.StringFilter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static com.fpt.ssds.constant.Constants.COMMON.USER;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final UserQueryService userQueryService;

    @PostMapping("/users")
    public ResponseEntity<ResponseDTO> getAllUserByRoleAndBranch(@RequestParam(name = "branchId") Long branchId, @RequestParam(name = "roleCode") String roleCode) {
        List<UserListingDTO> userDtos = userService.findUserByBranchAndRole(branchId, roleCode);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(userDtos));
    }

    @GetMapping("/available-specialist")
    public ResponseEntity<ResponseDTO> getAvailableSpecialistByTime(@RequestParam(name = "startTime") Long startTime,
                                                                    @RequestParam(name = "endTime") Long endTime,
                                                                    @RequestParam(name = "serviceId") Long serviceId,
                                                                    @RequestParam(name = "branchId") Long branchId) {
        List<UserListingDTO> userDtos = userService.getListAvailableSpecialistByTime(startTime, endTime, serviceId, branchId);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(userDtos));
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<ResponseDTO> getUserProfile(HttpServletRequest request) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(userService.getUserProfile(request)));
    }

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getByCriteria(UserCriteria criteria, Pageable pageable, HttpServletRequest httpServletRequest) {
        User user = (User) HTTPUtils.getAttribute(httpServletRequest, USER);
        if (Objects.nonNull(user.getBranch())) {
            StringFilter branchCodeFilter = new StringFilter();
            branchCodeFilter.setEquals(user.getBranch().getCode());

            criteria.setBranchCode(branchCodeFilter);
        }

        if (Objects.nonNull(criteria.getEmail())) {
            criteria.getEmail().setDoesNotContain(Constants.FAKE_EMAIL_PREFIX);
        }

        Page<UserDto> page = userQueryService.findByCriteria(criteria, pageable);
        ResponseDTO response = ResponseUtils.responseOK(page.getContent());
        response.getMeta().setTotal(page.getTotalElements());
        response.getMeta().setPage(page.getNumber());
        response.getMeta().setSize(page.getSize());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<ResponseDTO> createStaffAccount(@RequestBody @Valid UserDto userDTO) {
        ResponseDTO responseDTO = userService.createUser(userDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

//    @PostMapping(value = "/change-password")
//    public ResponseEntity<ResponseDTO> createStaffAccount(@RequestBody ) {
//        ResponseDTO responseDTO = userService.createUser(userDTO);
//        return ResponseEntity.ok().body(responseDTO);
//    }

    @PostMapping(value = "/change-password")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody UserDto userDTO) {
        return ResponseEntity.ok().body(userService.changePassword(userDTO));
    }

    @PostMapping(value = "/update-info")
    public ResponseEntity<ResponseDTO> updateInfo(@RequestBody UserDto userDTO) {
        return ResponseEntity.ok().body(userService.updateInfo(userDTO));
    }
}
