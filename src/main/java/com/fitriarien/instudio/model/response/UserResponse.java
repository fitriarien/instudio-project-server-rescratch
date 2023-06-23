package com.fitriarien.instudio.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {

    private String id;

    private String username;

    @JsonIgnore
    private String password;

    private String role;

    private String name;

    private String email;

    private String phone;

    private String address;

    private Long status;
}
