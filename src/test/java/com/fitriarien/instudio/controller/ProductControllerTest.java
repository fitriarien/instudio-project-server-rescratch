package com.fitriarien.instudio.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitriarien.instudio.configuration.JwtTokenUtil;
import com.fitriarien.instudio.entity.Product;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.CreateProductRequest;
import com.fitriarien.instudio.model.request.UpdateProductRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.ProductResponse;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "jwt.secret=fitriarien")
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
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
        productRepository.deleteAll();
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

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("person1");
        user.setPassword(passwordEncoder.encode("rahasia"));
        user.setRole("customer");
        user.setName("Person 1");
        user.setEmail("person1@example.com");
        user.setPhone("903859173");
        user.setAddress("Jakarta");
        user.setStatus(0L);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetListOfProductsEmpty() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        mockMvc.perform(
                get("/api/products/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNoContent()
        ).andDo(result -> {
            GenerateResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetListOfProductsSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");

        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setProductId(UUID.randomUUID().toString());
            product.setProductName("Kitchen Set " + (i+1));
            product.setProductModel("Letter L");
            product.setCostEstimation(15000000D);
            product.setProductStatus(1L);
            product.setUser(user);
            productRepository.save(product);
        }

        mockMvc.perform(
                get("/api/products/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
        });
    }

    @Test
    void testGetProductNotFound() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        mockMvc.perform(
                get("/api/products/wrongId")
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
    void testGetProductSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set A");
        product.setProductModel("Letter L");
        product.setCostEstimation(15000000D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);

        mockMvc.perform(
                get("/api/products/" + product.getProductId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(product.getProductId(), response.getData().getProductId());
            assertEquals(product.getProductName(), response.getData().getProductName());
            assertEquals(product.getProductModel(), response.getData().getProductModel());
            assertEquals(product.getCostEstimation(), response.getData().getCostEstimation());
            assertEquals(product.getProductStatus(), response.getData().getProductStatus());
        });
    }

    @Test
    void testCreateProductBadRequest() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        CreateProductRequest request = new CreateProductRequest();
        request.setProductName("");

        mockMvc.perform(
                post("/api/products/users/" + user.getId())
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
    void testCreateProductNotFoundUser() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        CreateProductRequest request = new CreateProductRequest();
        request.setProductName("Kitchen Set A");
        request.setProductModel("Letter L");
        request.setCostEstimation(15000000D);

        mockMvc.perform(
                post("/api/products/users/notAdmin")
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
    void testCreateProductForbiddenUser() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("person1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("person1");

        CreateProductRequest request = new CreateProductRequest();
        request.setProductName("Kitchen Set A");
        request.setProductModel("Letter L");
        request.setCostEstimation(15000000D);

        mockMvc.perform(
                post("/api/products/users/" + user.getId())
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
    void testCreateProductSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        CreateProductRequest request = new CreateProductRequest();
        request.setProductName("Kitchen Set A");
        request.setProductModel("Letter L");
        request.setCostEstimation(15000000D);

        mockMvc.perform(
                post("/api/products/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData().getProductId());
            assertEquals(request.getProductName(), response.getData().getProductName());
            assertEquals(request.getProductModel(), response.getData().getProductModel());
            assertEquals(request.getCostEstimation(), response.getData().getCostEstimation());
            assertEquals(1, response.getData().getProductStatus());
        });
    }

    @Test
    void testUpdateProductNotFoundUser() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set lama");
        product.setProductModel("lama");
        product.setCostEstimation(0D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);

        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductName("Kitchen Set A");
        request.setProductModel("Letter L");
        request.setCostEstimation(15000000D);

        mockMvc.perform(
                put("/api/products/"+ product.getProductId() +"/users/wrongId")
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
    void testUpdateProductForbiddenUser() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("person1");
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set lama");
        product.setProductModel("lama");
        product.setCostEstimation(0D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);

        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductName("Kitchen Set A");
        request.setProductModel("Letter L");
        request.setCostEstimation(15000000D);

        mockMvc.perform(
                put("/api/products/"+ product.getProductId() +"/users/" + user.getId())
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
    void testUpdateProductNotFound() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductName("Kitchen Set A");
        request.setProductModel("Letter L");
        request.setCostEstimation(15000000D);

        mockMvc.perform(
                put("/api/products/wrongId/users/" + user.getId())
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
    void testUpdateProductSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set lama");
        product.setProductModel("lama");
        product.setCostEstimation(0D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);

        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductName("Kitchen Set A");
        request.setProductModel("Letter L");
        request.setCostEstimation(15000000D);

        mockMvc.perform(
                put("/api/products/"+ product.getProductId() +"/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(product.getProductId(), response.getData().getProductId());
            assertEquals(request.getProductName(), response.getData().getProductName());
            assertEquals(request.getProductModel(), response.getData().getProductModel());
            assertEquals(request.getCostEstimation(), response.getData().getCostEstimation());
            assertEquals(1, response.getData().getProductStatus());
        });
    }

    @Test
    void testDeleteProductNotFoundUser() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set A");
        product.setProductModel("Letter L");
        product.setCostEstimation(15000000D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);

        mockMvc.perform(
                patch("/api/products/"+ product.getProductId() +"/users/wrongId")
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
    void testDeleteProductForbiddenUser() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("person1");
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set A");
        product.setProductModel("Letter L");
        product.setCostEstimation(15000000D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);

        mockMvc.perform(
                patch("/api/products/"+ product.getProductId() +"/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testDeleteProductNotFound() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set A");
        product.setProductModel("Letter L");
        product.setCostEstimation(15000000D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);

        mockMvc.perform(
                patch("/api/products/wrongId/users/" + user.getId())
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
    void testDeleteProductSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set A");
        product.setProductModel("Letter L");
        product.setCostEstimation(15000000D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);

        mockMvc.perform(
                patch("/api/products/"+ product.getProductId() +"/users/" + user.getId())
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
        });
    }

    @Test
    void testGetByPageOfProductsEmpty() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        mockMvc.perform(
                get("/api/products")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNoContent()
        ).andDo(result -> {
            GenerateResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetProductsByPageSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = new Product();

        for (int i = 0; i < 15; i++) {
            product.setProductId(UUID.randomUUID().toString());
            product.setProductName("Kitchen Set " + (i+1));
            product.setProductModel("Letter L");
            product.setCostEstimation(15000000D);
            product.setProductStatus(1L);   // status available
            product.setUser(user);
            productRepository.save(product);
        }

        for (int i = 0; i < 10; i++) {
            product.setProductId(UUID.randomUUID().toString());
            product.setProductName("Kitchen Set " + (i+16));
            product.setProductModel("Letter U");
            product.setCostEstimation(20000000D);
            product.setProductStatus(0L);   // status not available
            product.setUser(user);
            productRepository.save(product);
        }

        mockMvc.perform(
                get("/api/products")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(5, response.getData().size());
            assertEquals(1, response.getPaging().getCurrentPage());
            assertEquals(2, response.getPaging().getTotalPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }
}