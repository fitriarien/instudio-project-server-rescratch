package com.fitriarien.instudio.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitriarien.instudio.configuration.JwtTokenUtil;
import com.fitriarien.instudio.entity.Order;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.CreatePaymentRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.PaymentResponse;
import com.fitriarien.instudio.repository.OrderRepository;
import com.fitriarien.instudio.repository.PaymentRepository;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "jwt.secret=fitriarien")
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
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
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("person1");
        user.setPassword(passwordEncoder.encode("rahasia"));
        user.setRole("customer");
        user.setName("Person 1");
        user.setEmail("person1@example.com");
        user.setPhone("903859173");
        user.setAddress("Jakarta");
        user.setStatus(1L);
        userRepository.save(user);

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("admin1");
        user.setPassword(passwordEncoder.encode("rahasia"));
        user.setRole("admin");
        user.setName("Admin 1");
        user.setEmail("admin1@example.com");
        user.setPhone("903859173");
        user.setAddress("Jakarta");
        user.setStatus(0L);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreatePaymentBadRequest() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("person1");
        String token = jwtTokenUtil.generateToken(userDetails);
        User user = userRepository.findByUsername("person1");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderCode("TR1");
        order.setOrderDate("2023-06-23 18:32:30");
        order.setVisitSchedule("2023-07-02 10:00:00");
        order.setVisitAddress("Jakarta");
        order.setOrderAmount(0D);
        order.setOrderStatus(0L);
        order.setUser(user);
        orderRepository.save(order);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setPaymentAmount(0D);
        request.setPaymentMethod("");
        request.setPaymentDetail("");

        mockMvc.perform(
                post("/api/payments/orders/"+order.getOrderId()+"/users/" + user.getId())
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
    void testCreatePaymentNotFoundUser() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("person1");
        String token = jwtTokenUtil.generateToken(userDetails);
        User user = userRepository.findByUsername("person1");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderCode("TR1");
        order.setOrderDate("2023-06-23 18:32:30");
        order.setVisitSchedule("2023-07-02 10:00:00");
        order.setVisitAddress("Jakarta");
        order.setOrderAmount(0D);
        order.setOrderStatus(0L);
        order.setUser(user);
        orderRepository.save(order);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setPaymentAmount(5_000_000D);
        request.setPaymentMethod("Bank Transfer");
        request.setPaymentDetail("DP");
        request.setAccountNumber("78595880986");

        mockMvc.perform(
                post("/api/payments/orders/"+order.getOrderId()+"/users/wrongId")
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
    void testCreatePaymentForbiddenUser() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);
        User user = userRepository.findByUsername("admin1");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderCode("TR1");
        order.setOrderDate("2023-06-23 18:32:30");
        order.setVisitSchedule("2023-07-02 10:00:00");
        order.setVisitAddress("Jakarta");
        order.setOrderAmount(0D);
        order.setOrderStatus(0L);
        order.setUser(user);
        orderRepository.save(order);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setPaymentAmount(5_000_000D);
        request.setPaymentMethod("Bank Transfer");
        request.setPaymentDetail("DP");
        request.setAccountNumber("78595880986");

        mockMvc.perform(
                post("/api/payments/orders/"+order.getOrderId()+"/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isForbidden()
        ).andDo(result -> {
            GenerateResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreatePaymentNotFoundOrder() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("person1");
        String token = jwtTokenUtil.generateToken(userDetails);
        User user = userRepository.findByUsername("person1");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderCode("TR1");
        order.setOrderDate("2023-06-23 18:32:30");
        order.setVisitSchedule("2023-07-02 10:00:00");
        order.setVisitAddress("Jakarta");
        order.setOrderAmount(0D);
        order.setOrderStatus(0L);
        order.setUser(user);
        orderRepository.save(order);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setPaymentAmount(5_000_000D);
        request.setPaymentMethod("Bank Transfer");
        request.setPaymentDetail("DP");
        request.setAccountNumber("78595880986");

        mockMvc.perform(
                post("/api/payments/orders/wrongId/users/" + user.getId())
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
    void testCreatePaymentSuccess() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("person1");
        String token = jwtTokenUtil.generateToken(userDetails);
        User user = userRepository.findByUsername("person1");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderCode("TR1");
        order.setOrderDate("2023-06-23 18:32:30");
        order.setVisitSchedule("2023-07-02 10:00:00");
        order.setVisitAddress("Jakarta");
        order.setOrderAmount(0D);
        order.setOrderStatus(0L);
        order.setUser(user);
        orderRepository.save(order);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setPaymentAmount(5_000_000D);
        request.setPaymentMethod("Bank Transfer");
        request.setPaymentDetail("DP");
        request.setAccountNumber("78595880986");

        mockMvc.perform(
                post("/api/payments/orders/"+order.getOrderId()+"/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<PaymentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getPaymentId());
            assertNotNull(response.getData().getPaymentDate());
            assertEquals(request.getPaymentAmount(), response.getData().getPaymentAmount());
            assertEquals(request.getPaymentMethod(), response.getData().getPaymentMethod());
            assertEquals(request.getPaymentDetail(), response.getData().getPaymentDetail());
            assertEquals(request.getAccountNumber(), response.getData().getAccountNumber());
        });
    }
}