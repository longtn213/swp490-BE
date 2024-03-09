package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.service.UserService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.UserDto;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController("PublicUserController")
@RequestMapping("${ssds.api.ref.public}/web/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/create")
    public ResponseEntity<ResponseDTO> createUser(@RequestBody @Valid UserDto userDTO) {
        ResponseDTO responseDTO = userService.createUser(userDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody UserDto userDTO) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(userService.login(userDTO)));
    }

    @PostMapping(value = "/forgot-password")
    public ResponseEntity<ResponseDTO> forgotPassword(@RequestBody UserDto userDTO) {
        return ResponseEntity.ok().body(userService.forgotPassword(userDTO));
    }
}
