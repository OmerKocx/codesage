package com.omerkoc.main.service;

import com.omerkoc.main.dto.LoginResponse;
import com.omerkoc.main.dto.UserDto;
import com.omerkoc.main.dto.UserLoginRequest;
import com.omerkoc.main.dto.UserRegisterRequest;
import com.omerkoc.main.dto.UserLogoutRequest;

public interface IUserService {

    UserDto register(UserRegisterRequest request);

    LoginResponse login(UserLoginRequest request);

    void logout(UserLogoutRequest request);

}
