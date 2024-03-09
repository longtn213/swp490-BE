package com.fpt.ssds.service;

import com.fpt.ssds.domain.User;
import com.fpt.ssds.service.dto.Auth0LoginResponseDTO;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.UserDto;
import com.fpt.ssds.service.dto.UserListingDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User getCustomer(UserListingDTO customerDto);

    ResponseDTO createUser(UserDto userDTO);

    List<UserListingDTO> findUserByBranchAndRole(Long branchCode, String roleCode);

    User findByRoleAndId(Long id, String roleCode);

    Optional<User> findById(Long id);

    Auth0LoginResponseDTO login(UserDto userDTO);

    List<UserListingDTO> getListAvailableSpecialistByTime(Long startTime, Long endTime, Long serviceId, Long branchId);

    UserDto getUserProfile(HttpServletRequest request);

    ResponseDTO changePassword(UserDto userDTO);

    ResponseDTO updateInfo(UserDto userDTO);

    ResponseDTO forgotPassword(UserDto userDTO);
}
