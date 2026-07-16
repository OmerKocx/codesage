package com.omerkoc.main.controller;

import jakarta.validation.Valid;
import com.omerkoc.main.dto.LoginResponse;
import com.omerkoc.main.dto.UserDto;
import com.omerkoc.main.dto.UserLoginRequest;
import com.omerkoc.main.dto.UserRegisterRequest;
import com.omerkoc.main.dto.UserLogoutRequest;
import org.springframework.http.ResponseEntity;

public interface IUserController {

    ResponseEntity<UserDto> register(@Valid UserRegisterRequest request);

    ResponseEntity<LoginResponse> login(@Valid UserLoginRequest request);

    ResponseEntity<Void> logout(@Valid UserLogoutRequest request);

}
