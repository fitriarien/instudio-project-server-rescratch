package com.fitriarien.instudio.controller;

import com.fitriarien.instudio.configuration.JwtBlacklistFilter;
import com.fitriarien.instudio.model.request.RegisterUserRequest;
import com.fitriarien.instudio.model.request.LoginUserRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.TokenResponse;
import com.fitriarien.instudio.model.response.UserResponse;
import com.fitriarien.instudio.service.AuthService;
import com.fitriarien.instudio.service.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin("*")
public class AuthController {
	@Autowired
	private AuthService authService;
	@Autowired
	private TokenBlacklistService tokenBlacklistService;
	@Autowired
	private JwtBlacklistFilter jwtBlacklistFilter;

	@PostMapping(
			path = "/api/users",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public GenerateResponse<UserResponse> register(@RequestBody RegisterUserRequest request) {
		UserResponse userResponse = authService.register(request);
		return GenerateResponse.<UserResponse>builder().data(userResponse).build();
	}

	@PostMapping(
			path = "/api/auth/login",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public GenerateResponse<TokenResponse> login(@RequestBody LoginUserRequest request) throws Exception {
		TokenResponse tokenResponse = authService.login(request);
		return GenerateResponse.<TokenResponse>builder().data(tokenResponse).build();
	}

	@PostMapping(
			path = "/api/auth/logout",
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public GenerateResponse<String> logout(HttpServletRequest request) {
		String token = jwtBlacklistFilter.extractToken(request);
		tokenBlacklistService.addToBlacklist(token);
		return GenerateResponse.<String>builder().data("success").build();
	}
}