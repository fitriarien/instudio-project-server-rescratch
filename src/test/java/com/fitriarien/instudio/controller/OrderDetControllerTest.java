package com.fitriarien.instudio.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitriarien.instudio.configuration.JwtTokenUtil;
import com.fitriarien.instudio.entity.Order;
import com.fitriarien.instudio.entity.Product;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.CreateOrderDetRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.OrderResponse;
import com.fitriarien.instudio.repository.OrderDetRepository;
import com.fitriarien.instudio.repository.OrderRepository;
import com.fitriarien.instudio.repository.ProductRepository;
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
class OrderDetControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderDetRepository orderDetRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
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
        orderDetRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
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

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("admin2");
        user.setPassword(passwordEncoder.encode("rahasia"));
        user.setRole("admin");
        user.setName("Admin 2");
        user.setEmail("admin2@example.com");
        user.setPhone("98658798");
        user.setAddress("Jakarta");
        user.setStatus(1L);
        userRepository.save(user);

        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set A");
        product.setProductModel("Letter L");
        product.setCostEstimation(15000000D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);
    }

    @AfterEach
    void tearDown() {
        orderDetRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateOrderDetailBadRequest() throws Exception {
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

        CreateOrderDetRequest request = new CreateOrderDetRequest();
        request.setProductName("");
        request.setProductSize(0D);
        request.setProductTheme("");
        request.setProductCost(0D);
        request.setTimeEstimation(0L);

        mockMvc.perform(
                post("/api/orders/"+order.getOrderId()+"/users/" + user.getId())
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
    void testCreateOrderDetailNotFoundUser() throws Exception {
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

        CreateOrderDetRequest request = new CreateOrderDetRequest();
        request.setProductName("Kitchen Set A");
        request.setProductSize(7.5);
        request.setProductTheme("Industrial");
        request.setProductCost(10_000_000D);
        request.setTimeEstimation(6L);

        mockMvc.perform(
                post("/api/orders/"+order.getOrderId()+"/users/wrongId")
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
    void testCreateOrderDetailForbiddenUser() throws Exception {
        //customer
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

        CreateOrderDetRequest request = new CreateOrderDetRequest();
        request.setProductName("Kitchen Set A");
        request.setProductSize(7.5);
        request.setProductTheme("Industrial");
        request.setProductCost(10_000_000D);
        request.setTimeEstimation(6L);

        mockMvc.perform(
                post("/api/orders/"+order.getOrderId()+"/users/"+user.getId())
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

        //status = 0
        userDetails = authService.loadUserByUsername("person1");
        token = jwtTokenUtil.generateToken(userDetails);
        user = userRepository.findByUsername("person1");

        mockMvc.perform(
                post("/api/orders/"+order.getOrderId()+"/users/"+user.getId())
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
    void testCreateOrderDetailNotFoundOrder() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin2");
        String token = jwtTokenUtil.generateToken(userDetails);
        User user = userRepository.findByUsername("admin2");

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

        CreateOrderDetRequest request = new CreateOrderDetRequest();
        request.setProductName("Kitchen Set A");
        request.setProductSize(7.5);
        request.setProductTheme("Industrial");
        request.setProductCost(10_000_000D);
        request.setTimeEstimation(6L);

        mockMvc.perform(
                post("/api/orders/wrongId/users/"+user.getId())
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
    void testCreateOrderDetailSuccess() throws Exception {
        UserDetails userDetails = authService.loadUserByUsername("admin2");
        String token = jwtTokenUtil.generateToken(userDetails);
        User user = userRepository.findByUsername("admin2");

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

        CreateOrderDetRequest request = new CreateOrderDetRequest();
        request.setProductName("Kitchen Set A");
        request.setProductSize(7.5);
        request.setProductTheme("Industrial");
        request.setProductCost(10_000_000D);
        request.setTimeEstimation(6L);

        mockMvc.perform(
                post("/api/orders/"+order.getOrderId()+"/users/"+user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData());
        });
    }
}