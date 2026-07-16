package com.omerkoc.main.controller;

import com.omerkoc.main.dto.LoginResponse;
import com.omerkoc.main.dto.UserDto;
import com.omerkoc.main.dto.UserLoginRequest;
import com.omerkoc.main.dto.UserRegisterRequest;
import org.springframework.http.ResponseEntity;

public interface IUserController {

    ResponseEntity<UserDto> register(UserRegisterRequest request);

    ResponseEntity<LoginResponse> login(UserLoginRequest request);

}
