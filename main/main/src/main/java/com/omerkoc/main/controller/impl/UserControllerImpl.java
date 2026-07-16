package com.omerkoc.main.controller.impl;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omerkoc.main.controller.IUserController;
import com.omerkoc.main.dto.LoginResponse;
import com.omerkoc.main.dto.UserDto;
import com.omerkoc.main.dto.UserLoginRequest;
import com.omerkoc.main.dto.UserLogoutRequest;
import com.omerkoc.main.dto.UserRegisterRequest;
import com.omerkoc.main.service.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserControllerImpl implements IUserController {

    private final IUserService userService;

    @PostMapping("/register")
    @Override
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterRequest request) {
        UserDto registeredUser = userService.register(request);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/logout")
    @Override
    public ResponseEntity<Void> logout(@Valid @RequestBody UserLogoutRequest request) {
        userService.logout(request);
        return ResponseEntity.ok().build();
    }

}
