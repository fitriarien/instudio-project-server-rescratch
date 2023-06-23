package com.fitriarien.instudio.service;

import com.fitriarien.instudio.model.request.LoginUserRequest;
import com.fitriarien.instudio.model.request.RegisterUserRequest;
import com.fitriarien.instudio.model.response.TokenResponse;
import com.fitriarien.instudio.model.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterUserRequest request);

    TokenResponse login(LoginUserRequest request) throws Exception;
}
