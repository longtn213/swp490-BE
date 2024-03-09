package com.fpt.ssds.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fpt.ssds.common.exception.SSDSAuthorizationException;
import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.common.exception.TokenException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.AppointmentService;
import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.domain.Role;
import com.fpt.ssds.domain.User;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.repository.AppointmentServiceRepository;
import com.fpt.ssds.repository.BranchRepository;
import com.fpt.ssds.repository.RoleRepository;
import com.fpt.ssds.repository.UserRepository;
import com.fpt.ssds.service.ApiService;
import com.fpt.ssds.service.FileService;
import com.fpt.ssds.service.UserService;
import com.fpt.ssds.service.dto.*;
import com.fpt.ssds.service.mapper.UserListingMapper;
import com.fpt.ssds.service.mapper.UserMapper;
import com.fpt.ssds.utils.DateUtils;
import com.fpt.ssds.utils.HTTPUtils;
import com.fpt.ssds.utils.ResponseUtils;
import com.fpt.ssds.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fpt.ssds.constant.ErrorConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final AppointmentServiceRepository appointmentServiceRepository;
    @Value("${ssds.config.timezone}")
    String systemTimezone;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserListingMapper userListingMapper;

    private final ApiService apiService;

    private final RoleRepository roleRepository;

    private final BranchRepository branchRepository;

    private final MessageSource messageSource;

    @Value("${ssds.config.auth0.base-domain}")
    String auth0BaseDomain;

    @Value("${ssds.config.auth0.client-id}")
    String clientId;

    @Value("${ssds.config.auth0.client-secret}")
    String auth0ClientSecret;

    private final FileService fileService;

    @Override
    public User getCustomer(UserListingDTO customerDto) {
        if (Objects.nonNull(customerDto.getPhoneNumber())) {
            Optional<User> userOpt = userRepository.findByPhoneNumber(customerDto.getPhoneNumber());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return user;
            }
            return userRepository.save(createNewCustomer(customerDto));
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseDTO createUser(UserDto userDTO) {
        userDTO.getPassword().trim();
        validateCreateUser(userDTO);
        userDTO.setEmail(StringUtils.isNotEmpty(userDTO.getEmail()) ? userDTO.getEmail() : Constants.FAKE_EMAIL_PREFIX + userDTO.getUsername() + "@gmail.com");
        ResponseDTO responseDTO = ResponseUtils.responseOK(null);
        Auth0CreateUserRequestDTO requestDTO = new Auth0CreateUserRequestDTO();
        requestDTO.setUsername(userDTO.getUsername());
        requestDTO.setPassword(userDTO.getPassword());
        requestDTO.setEmail(userDTO.getEmail());
        requestDTO.setConnection("Username-Password-Authentication");
        requestDTO.setVerifyEmail(Boolean.FALSE);
        requestDTO.setEmailVerified(Boolean.FALSE);
        requestDTO.setClientId(clientId);
        String urlSignup = auth0BaseDomain + "/dbconnections/signup";

        User user = new User();

        if (Objects.nonNull(userDTO.getRole())) {
            Optional<Role> roleOpt = roleRepository.findByCode(userDTO.getRole().getCode());
            user.setRole(roleOpt.get());
        }
        if (Objects.nonNull(userDTO.getBranch())) {
            Optional<Branch> branchOpt = branchRepository.findByCode(userDTO.getBranch().getCode());
            user.setBranch(branchOpt.get());
        }

        Optional<User> userOpt = userRepository.findByPhoneNumberAndRefIdIsNull(userDTO.getPhoneNumber());
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            // success => save user
            if (Objects.isNull(user.getRole())) {
                user = createNewCustomer(userDTO);
            }
        }
        user.setFullName(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setDob(userDTO.getDob());
        user.setGender(userDTO.getGender());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setIsActive(Boolean.TRUE);

        try {
            // add user auth0
            ResponseEntity<Auth0SignupDTO> response =
                (ResponseEntity<Auth0SignupDTO>) apiService.postRequest(urlSignup, requestDTO, Auth0SignupDTO.class);
            Auth0SignupDTO userResponseDTO = response.getBody();
            user.setRefId(userResponseDTO.getRefId());
            userRepository.save(user);
            if (Objects.nonNull(userDTO.getAvatar())) {
                fileService.updateFileRefId(Arrays.asList(userDTO.getAvatar()), user.getId());
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                apiService.resetToken();
            }
            log.error("SSDS createUser error: {}", e.getMessage(), e);
            List<FieldErrorDTO> fieldErrorDTOS = new ArrayList<>();
            Auth0SignupDTO userResponseDTO = Utils.convertJsonStringToObject(e.getResponseBodyAsString(), Auth0SignupDTO.class);

            throw new SSDSBusinessException(INPUT_INVALID, null, fieldErrorDTOS);
        }
        return responseDTO;
    }

    private User createNewCustomer(UserDto userDTO) {
        User user = userMapper.toEntity(userDTO);
        Optional<Role> roleCustomer = roleRepository.findByCode(Constants.ROLE.CUSTOMER);
        user.setRole(roleCustomer.get());
        return user;
    }

    @Override
    @Transactional
    public List<UserListingDTO> findUserByBranchAndRole(Long branchId, String roleCode) {
        List<User> users = getUserByBranchAndRole(branchId, roleCode);
        return userListingMapper.toDto(users);
    }

    @Override
    @Transactional
    public User findByRoleAndId(Long id, String roleCode) {
        Optional<User> userOpt = userRepository.findByRoleAndId(roleCode, id);
        if (userOpt.isEmpty()) {
            throw new SSDSBusinessException(USER_NOT_EXIST);
        }
        return userOpt.get();
    }

    @Override
    @Transactional
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Auth0LoginResponseDTO login(UserDto userDTO) {
        Optional<User> userOpt = userRepository.getUserByUsername(userDTO.getUsername());
        if (userOpt.isEmpty()) {
            throw new SSDSBusinessException(USER_NOT_EXIST);
        }
        Auth0UserLoginRequestDTO requestDTO = new Auth0UserLoginRequestDTO();
        requestDTO.setUsername(userOpt.get().getEmail());
        requestDTO.setPassword(userDTO.getPassword());
        requestDTO.setAudience("SSDS_ID");
        requestDTO.setClientId(clientId);
        requestDTO.setClientSecret(auth0ClientSecret);
        String loginUrl = auth0BaseDomain + "/oauth/token";

        try {
            ResponseEntity<Auth0LoginResponseDTO> response =
                (ResponseEntity<Auth0LoginResponseDTO>) apiService.postRequest(loginUrl, requestDTO, Auth0LoginResponseDTO.class);
            Auth0LoginResponseDTO loginResponseDTO = response.getBody();
            return loginResponseDTO;
        } catch (HttpStatusCodeException e) {
            String errorMessage = "";
            Auth0CreateUserResponseDTO userResponseDTO = Utils.convertJsonStringToObject(e.getResponseBodyAsString(), Auth0CreateUserResponseDTO.class);
            if (userResponseDTO.getErrorDescription().contains("Wrong email or password")) {
                errorMessage = "Tên đăng nhập hoặc mật khẩu không chính xác. Vui lòng kiểm tra và thử lại";
            } else {
                errorMessage = userResponseDTO.getErrorDescription();
            }
            throw new SSDSBusinessException(null, errorMessage);
        }

    }

    @Override
    @Transactional
    public List<UserListingDTO> getListAvailableSpecialistByTime(Long startTime, Long endTime, Long serviceId, Long branchId) {

        Map<String, Instant> instantMap = DateUtils.zonedDateTimeToInstant(
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.of(systemTimezone)),
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.of(systemTimezone)),
            systemTimezone);
        Instant instantStartTime = instantMap.get("startTime");
        Instant instantEndTime = instantMap.get("endTime");

        Optional<Branch> branchOpt = branchRepository.findById(branchId);
        if (branchOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.BRANCH_NOT_EXIST);
        }


        List<User> allSpecialists = getUserByBranchAndRole(branchId, Constants.ROLE.SPECIALIST);
        List<User> specialistNotAvailable = findSpecialistNotAvailableByTime(instantStartTime, instantEndTime, serviceId, branchId);
        List<Long> specialistNotAvailableId = specialistNotAvailable.stream().map(User::getId).collect(Collectors.toList());
        List<User> availableSpecialist = allSpecialists.stream().filter(specialist -> !specialistNotAvailableId.contains(specialist.getId())).collect(Collectors.toList());
        return userListingMapper.toDto(availableSpecialist);
    }

    @Override
    public UserDto getUserProfile(HttpServletRequest request) {
        String token = HTTPUtils.resolveToken(request);
        if (StringUtils.isEmpty(token)) {
            throw new TokenException(TOKEN_INVALID);
        }

        DecodedJWT jwt = JWT.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        String refId = claims.get("sub").asString().replace("auth0|", "");
        if (refId == null || refId.isEmpty()) {
            throw new SSDSAuthorizationException(USER_NOT_EXIST);
        }

        List<com.fpt.ssds.domain.User> users = userRepository.findByRefId(refId);
        User user = users.get(0);
        UserDto userDto = userMapper.toDto(user);
        List<FileDto> avatar = fileService.findByTypeAndRefIdAndUploadStatus(FileType.AVATAR, user.getId(), UploadStatus.SUCCESS);
        if (CollectionUtils.isNotEmpty(avatar)) {
            userDto.setAvatar(avatar.get(0));
        }
        if (userDto.getEmail().startsWith(Constants.FAKE_EMAIL_PREFIX)) {
            userDto.setEmail(null);
        }
        return userDto;
    }

    @Override
    public ResponseDTO changePassword(UserDto userDTO) {
        ResponseDTO responseDTO = ResponseUtils.responseOK(null);
        Optional<User> userOpt = userRepository.getUserByUsername(userDTO.getUsername());
        if (userOpt.isEmpty()) {
            throw new SSDSBusinessException(USER_NOT_EXIST);
        }
        Auth0ChangePasswordRequestDTO requestDTO = new Auth0ChangePasswordRequestDTO();

        requestDTO.setPassword(userDTO.getNewPassword());
        requestDTO.setConnection("Username-Password-Authentication");
        String changePasswordUrl = auth0BaseDomain + "/api/v2/users/auth0|" + userOpt.get().getRefId();

        try {
            HttpHeaders headerAuth0 = apiService.getHeaderAuth0();
            headerAuth0.add("content-type", "application/json");
            ResponseEntity<Auth0CreateUserResponseDTO> response =
                (ResponseEntity<Auth0CreateUserResponseDTO>) apiService.patchRequest(changePasswordUrl, headerAuth0, requestDTO, Auth0CreateUserResponseDTO.class);
            Auth0CreateUserResponseDTO loginResponseDTO = response.getBody();
        } catch (HttpStatusCodeException e) {
            Auth0CreateUserResponseDTO userResponseDTO = Utils.convertJsonStringToObject(e.getResponseBodyAsString(), Auth0CreateUserResponseDTO.class);
            String errorMessage = userResponseDTO.getMessage();
            if (errorMessage.contains("Password is too weak")) {
                errorMessage = "Mật khẩu cần chứa ít nhất 8 kí tự, bao gồm chữ hoa, chữ thường, số và kí tự đặc biệt.";
            }
            throw new SSDSBusinessException(null, errorMessage);
        }
        return responseDTO;
    }

    @Override
    @Transactional
    public ResponseDTO updateInfo(UserDto userDTO) {
        ResponseDTO responseDTO = ResponseUtils.responseOK(null);
        Optional<User> userOpt = findById(userDTO.getId());
        if (userOpt.isEmpty()) {
            throw new SSDSBusinessException(USER_NOT_EXIST);
        }
        User user = userOpt.get();
        if (Objects.nonNull(userDTO.getRole())) {
            Optional<Role> roleOpt = roleRepository.findByCode(userDTO.getRole().getCode());
            user.setRole(roleOpt.get());
        }
        if (Objects.nonNull(userDTO.getBranch())) {
            Optional<Branch> branchOpt = branchRepository.findByCode(userDTO.getBranch().getCode());
            user.setBranch(branchOpt.get());
        }

        List<Long> usersid = userRepository.findByPhoneNumberOrEmailAndIdNotIn(userDTO.getPhoneNumber(), userDTO.getEmail(), Arrays.asList(userDTO.getId()));
        if (CollectionUtils.isNotEmpty(usersid)) {
            throw new SSDSBusinessException(null, "Số điện thoại hoặc email đã tồn tại trên hệ thống. Vui lòng kiểm tra và thử lại");
        }

        if (StringUtils.isNotEmpty(userDTO.getPhoneNumber())) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (Objects.nonNull(userDTO.getDob())) {
            user.setDob(userDTO.getDob());
        }
        if (Objects.nonNull(userDTO.getGender())) {
            user.setGender(userDTO.getGender());
        }
        if (StringUtils.isNotEmpty(userDTO.getPhoneNumber())) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (StringUtils.isNotEmpty(user.getEmail())) {
            if (user.getEmail().startsWith(Constants.FAKE_EMAIL_PREFIX) && StringUtils.isNotEmpty(userDTO.getEmail())) {
                auth0UpdateEmail(user, userDTO);
                user.setEmail(userDTO.getEmail());
            }
        }
        if (Objects.nonNull(userDTO.getAvatar())) {
            fileService.updateFileRefId(Arrays.asList(userDTO.getAvatar()), user.getId());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO forgotPassword(UserDto userDTO) {
        ResponseDTO responseDTO = ResponseUtils.responseOK(null);
        Optional<User> userOpt = userRepository.getUserByUsername(userDTO.getUsername());
        if (userOpt.isEmpty()) {
            throw new SSDSBusinessException(USER_NOT_EXIST);
        }

        User user = userOpt.get();
        if (Objects.isNull(user.getEmail())) {
            throw new SSDSBusinessException(null, "Tài khoản của bạn hiện chưa có thông tin email khôi phục mật khẩu. Vui lòng liên hệ spa để được hỗ trợ cài đặt lại mật khẩu mặc định");
        }
        if (user.getEmail().startsWith(Constants.FAKE_EMAIL_PREFIX)) {
            throw new SSDSBusinessException(null, "Tài khoản của bạn hiện chưa có thông tin email khôi phục mật khẩu. Vui lòng liên hệ spa để được hỗ trợ cài đặt lại mật khẩu mặc định");
        }

        Auth0CreateUserRequestDTO requestDTO = new Auth0CreateUserRequestDTO();

        requestDTO.setClientId(clientId);
        requestDTO.setEmail(user.getEmail());
        requestDTO.setConnection("Username-Password-Authentication");
        String changePasswordUrl = auth0BaseDomain + "/dbconnections/change_password";

        try {
            HttpHeaders headerAuth0 = new HttpHeaders();
            headerAuth0.add("content-type", "application/json");
            apiService.postRequest(changePasswordUrl, headerAuth0, requestDTO, String.class);
        } catch (Exception e) {
            throw new SSDSBusinessException(null, "Error while forgotPassword");
        }
        return responseDTO;
    }

    private void auth0UpdateEmail(User user, UserDto userDTO) {
        Auth0CreateUserRequestDTO requestDTO = new Auth0CreateUserRequestDTO();
        requestDTO.setEmail(user.getEmail());
        String urlSignup = auth0BaseDomain + "/api/v2/users/auth0|" + user.getRefId();
        try {
            // add user auth0
            HttpHeaders headerAuth0 = apiService.getHeaderAuth0();
            headerAuth0.add("content-type", "application/json");
            ResponseEntity<Auth0CreateUserResponseDTO> response =
                (ResponseEntity<Auth0CreateUserResponseDTO>) apiService.patchRequest(urlSignup, headerAuth0, requestDTO, Auth0CreateUserResponseDTO.class);
            Auth0CreateUserResponseDTO responseDTO = response.getBody();

        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                apiService.resetToken();
            }
            log.error("SSDS updateUser error: {}", e.getMessage(), e);
            List<FieldErrorDTO> fieldErrorDTOS = new ArrayList<>();
            Auth0SignupDTO auth0UpdateEmail = Utils.convertJsonStringToObject(e.getResponseBodyAsString(), Auth0SignupDTO.class);

            throw new SSDSBusinessException(INPUT_INVALID, null, fieldErrorDTOS);
        }
    }

    private List<User> findSpecialistNotAvailableByTime(Instant startTime, Instant endTime, Long serviceId, Long branchId) {
        List<Long> specialistId = userRepository.findSpecialistNotAvailableByTime(startTime, endTime, branchId);
        AppointmentService appointmentService = appointmentServiceRepository.findById(serviceId).get();
        if (Objects.nonNull(appointmentService.getSpecialist())) {
            specialistId.removeIf(id -> id.equals(appointmentService.getSpecialist().getId()));
        }
        return userRepository.findByIdIn(specialistId);
    }

    private void validateCreateUser(UserDto userDTO) {
        if (!Utils.validatePhoneNumber(userDTO.getPhoneNumber())) {
            throw new SSDSBusinessException(ErrorConstants.PHONE_NUMBER_INVALID);
        }
        List<User> users = userRepository.findByUsernameOrEmailOrPhoneNumber(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPhoneNumber());
        if (CollectionUtils.isNotEmpty(users)) {
            Optional<User> userOpt = users.stream().filter(user -> userDTO.getUsername().equals(user.getUsername())).findFirst();
            if (userOpt.isPresent()) {
                throw new SSDSBusinessException(ErrorConstants.USERNAME_EXISTED);
            }
            userOpt = users.stream().filter(user -> (Objects.nonNull(userDTO.getEmail()) && userDTO.getEmail().equals(user.getEmail())) || (userDTO.getPhoneNumber().equals(user.getPhoneNumber()) && Objects.nonNull(user.getRefId()))).findFirst();
            if (userOpt.isPresent()) {
                throw new SSDSBusinessException(ErrorConstants.ACCOUNT_EXISTED);
            }
        }
    }

    private User createNewCustomer(UserListingDTO userDTO) {
        User user = userListingMapper.toEntity(userDTO);
        user.setIsActive(true);
        Optional<Role> roleCustomer = roleRepository.findByCode(Constants.ROLE.CUSTOMER);
        user.setRole(roleCustomer.get());
        return user;
    }

    private List<User> getUserByBranchAndRole(Long branchId, String roleCode) {
        List<User> users = new ArrayList<>();
        if (Arrays.asList(Constants.ROLE.CUSTOMER, Constants.ROLE.MANAGER).contains(roleCode)) {
            users = userRepository.findByRole(roleCode);
        } else {
            users = userRepository.findByRoleAndBranch(roleCode, branchId);
        }
        return users;
    }
}
