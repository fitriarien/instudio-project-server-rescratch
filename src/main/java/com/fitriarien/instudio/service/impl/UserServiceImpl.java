package com.fitriarien.instudio.service.impl;

import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.UpdateUserRequest;
import com.fitriarien.instudio.model.response.UserResponse;
import com.fitriarien.instudio.repository.UserRepository;
import com.fitriarien.instudio.service.UserService;
import com.fitriarien.instudio.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Override
    @Transactional(readOnly = true)
    public UserResponse get(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        return toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(String id, UpdateUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        userRepository.save(user);
        return toUserResponse(user);
    }

    @Override
    @Transactional
    public void delete(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        user.setStatus(0L);
        userRepository.save(user);
    }

    private UserResponse toUserResponse(User user) {
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
}
