package com.fitriarien.instudio.service.impl;

import com.fitriarien.instudio.configuration.JwtTokenUtil;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.response.TokenResponse;
import com.fitriarien.instudio.model.response.UserResponse;
import com.fitriarien.instudio.model.request.RegisterUserRequest;
import com.fitriarien.instudio.model.request.LoginUserRequest;
import com.fitriarien.instudio.model.JwtToken;
import com.fitriarien.instudio.repository.UserRepository;
import com.fitriarien.instudio.service.AuthService;
import com.fitriarien.instudio.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements UserDetailsService, AuthService {

	public static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private ValidationService validationService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Loading User : {}", username);
		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
	}

	@Transactional
	@Override
	public UserResponse register(RegisterUserRequest request) {
		validationService.validate(request);

		boolean regexUsername = usernameRegex(request.getUsername());
		boolean regexPassword = passwordRegex(request.getPassword());
		boolean isExist = existUsername(request.getUsername());

		if (regexUsername && regexPassword) {
			if (!isExist) {
				User newUser = new User();
				newUser.setId(UUID.randomUUID().toString());
				newUser.setUsername(request.getUsername());
				newUser.setPassword(bcryptEncoder.encode(request.getPassword()));
				newUser.setName(request.getName());
				newUser.setRole(request.getRole());
				newUser.setEmail(request.getEmail());
				newUser.setPhone(request.getPhone());
				newUser.setAddress(request.getAddress());
				newUser.setStatus(1L);

				userRepository.save(newUser);
				return toResponse(newUser);
			} else {
				throw new ResponseStatusException(HttpStatus.IM_USED, "Username has been already used. Please change the username.");
			}
		} else if (!regexUsername && regexPassword) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong regex. Please re-enter the username.");
		} else if (regexUsername && !regexPassword) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong regex. Please re-enter the password.");
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong regex. Please re-enter username and the password.");
		}
	}

	private UserResponse toResponse(User user) {
		return UserResponse.builder()
				.id(user.getId())
				.username(user.getUsername())
				.name(user.getName())
				.role(user.getRole())
				.email(user.getEmail())
				.address(user.getAddress())
				.phone(user.getPhone())
				.status(user.getStatus())
				.build();
	}

	public boolean usernameRegex(String username) {
		boolean regexUsername;
		boolean uname_regex = Pattern.matches("^(?=.*?[a-z]).{5,}$", username);
		if (uname_regex) {
			regexUsername = true;
		} else {
			regexUsername = false;
		}
		return regexUsername;
	}

	public boolean passwordRegex(String password) {
		boolean regexPassword;
		boolean pass_regex = Pattern.matches("^(?=.*?[a-z]).{6,}$",
				password);
		if (pass_regex) {
			regexPassword = true;
		} else {
			regexPassword = false;
		}
		return regexPassword;
	}

	public boolean existUsername(String username) {
		boolean isExist = false;
		List<User> users = userRepository.findAll();
		for (User curr: users) {
			if (username.equals(curr.getUsername())) {
				isExist = true;
				break;
			} else {
				isExist = false;
			}
		}
		return isExist;
	}

	public void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@Transactional
	@Override
	public TokenResponse login(LoginUserRequest request) throws Exception {
		validationService.validate(request);

		// check registered username
		final User user = userRepository.findByUsername(request.getUsername());
		if (user == null) {
			logger.error("Unable to login. Username of {} is not found.", request.getUsername());
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login Failed: Username & password doesn't match.");
		}
		// check password
		if (!(bcryptEncoder.matches(request.getPassword(), user.getPassword()))) {
			logger.error("Unable to login. Password is wrong.");
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login Failed: Username & password doesn't match.");
		}

		// authenticate
		authenticate(request.getUsername(), request.getPassword());
		// load and set userDetails
		final UserDetails userDetails = loadUserByUsername(request.getUsername());
		// load and set token
		final JwtToken jwtToken = new JwtToken(jwtTokenUtil.generateToken(userDetails));

		return TokenResponse.builder()
				.id(user.getId())
				.username(user.getUsername())
				.name(user.getName())
				.token(jwtToken.getToken())
				.role(user.getRole())
				.status(user.getStatus())
				.build();
	}
}