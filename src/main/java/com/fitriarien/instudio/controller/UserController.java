package com.fitriarien.instudio.controller;

import com.fitriarien.instudio.model.request.UpdateUserRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.UserResponse;
import com.fitriarien.instudio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(
            path = "/api/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<UserResponse> get(@PathVariable("userId") String id) {
        UserResponse userResponse = userService.get(id);
        return GenerateResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PutMapping(
            path = "/api/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<UserResponse> update(@PathVariable("userId") String id,
                                                 @RequestBody UpdateUserRequest request) {
        UserResponse userResponse = userService.update(id, request);
        return GenerateResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(
            path = "/api/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<String> delete(@PathVariable("userId") String id) {
        userService.delete(id);
        return GenerateResponse.<String>builder().data("DELETED").build();
    }
}
