package com.fitriarien.instudio.service;

import com.fitriarien.instudio.model.request.UpdateUserRequest;
import com.fitriarien.instudio.model.response.UserResponse;

public interface UserService {
    UserResponse get(String id);

    UserResponse update(String id, UpdateUserRequest request);

    void delete(String id);
}
