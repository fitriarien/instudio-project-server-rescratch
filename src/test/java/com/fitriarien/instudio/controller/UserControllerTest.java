package com.fitriarien.instudio.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitriarien.instudio.configuration.JwtTokenUtil;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.UpdateUserRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.UserResponse;
import com.fitriarien.instudio.repository.UserRepository;
import com.fitriarien.instudio.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "jwt.secret=fitriarien")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("admin1");
        user.setPassword(passwordEncoder.encode("rahasia"));
        user.setRole("admin");
        user.setName("Admin 1");
        user.setEmail("admin1@example.com");
        user.setPhone("903859173");
        user.setAddress("Jakarta");
        user.setStatus(1L);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testGetUserNotFound() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        mockMvc.perform(
                get("/api/users/wrongId")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            GenerateResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetUserSuccess() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");

        mockMvc.perform(
                get("/api/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(user.getId(), response.getData().getId());
            assertEquals(user.getUsername(), response.getData().getUsername());
            assertEquals(user.getName(), response.getData().getName());
            assertEquals(user.getEmail(), response.getData().getEmail());
            assertEquals(user.getPhone(), response.getData().getPhone());
            assertEquals(user.getAddress(), response.getData().getAddress());
            assertEquals(user.getRole(), response.getData().getRole());
            assertEquals(user.getStatus(), response.getData().getStatus());
        });
    }

    @Test
    void testUpdateUserBadRequest() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("wrong-format");

        mockMvc.perform(
                put("/api/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            GenerateResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("admin1@gmail.com");

        mockMvc.perform(
                put("/api/users/wrongId")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            GenerateResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Admin Baru 1");
        request.setEmail("adminbaru@example.com");
        request.setPhone("9470582342");
        request.setAddress("Tangerang");

        mockMvc.perform(
                put("/api/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(request.getName(), response.getData().getName());
            assertEquals(request.getEmail(), response.getData().getEmail());
            assertEquals(request.getPhone(), response.getData().getPhone());
            assertEquals(request.getAddress(), response.getData().getAddress());
        });
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        mockMvc.perform(
                patch("/api/users/wrongId")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            GenerateResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");

        mockMvc.perform(
                patch("/api/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals("DELETED", response.getData());

            User userdb = userRepository.findById(user.getId()).orElse(null);
            assertNotNull(userdb);
            assertEquals(0, userdb.getStatus());
        });
    }
}