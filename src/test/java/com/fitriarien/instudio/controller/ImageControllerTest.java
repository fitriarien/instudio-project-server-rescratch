package com.fitriarien.instudio.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitriarien.instudio.configuration.JwtTokenUtil;
import com.fitriarien.instudio.entity.Image;
import com.fitriarien.instudio.entity.Product;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.UploadImageRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.ImageResponse;
import com.fitriarien.instudio.repository.ImageRepository;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "jwt.secret=fitriarien")
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ImageRepository imageRepository;
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
        imageRepository.deleteAll();
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

        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Kitchen Set A");
        product.setProductModel("Letter L");
        product.setCostEstimation(15000000D);
        product.setProductStatus(1L);
        product.setUser(user);
        productRepository.save(product);

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
        imageRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testUploadImageBadRequest() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        UploadImageRequest request = new UploadImageRequest();
        request.setImagePath("");

        mockMvc.perform(
                post("/api/images/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            GenerateResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<GenerateResponse<ImageResponse>>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUploadImageUserNotFound() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        UploadImageRequest request = new UploadImageRequest();
        request.setImageAlt("kitchen-set-A");
        request.setImagePath("www.example.com");
        request.setProductName("Kitchen Set A");

        mockMvc.perform(
                post("/api/images/users/wrong")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            GenerateResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<GenerateResponse<ImageResponse>>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUploadImageForbiddenUser() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("person1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("person1");

        UploadImageRequest request = new UploadImageRequest();
        request.setImageAlt("kitchen-set-A");
        request.setImagePath("www.example.com");
        request.setProductName("Kitchen Set A");

        mockMvc.perform(
                post("/api/images/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isForbidden()
        ).andDo(result -> {
            GenerateResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<GenerateResponse<ImageResponse>>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUploadImageProductNotFound() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");

        UploadImageRequest request = new UploadImageRequest();
        request.setImageAlt("kitchen-set-B");
        request.setImagePath("www.example.com");
        request.setProductName("Kitchen Set B");

        mockMvc.perform(
                post("/api/images/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            GenerateResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<GenerateResponse<ImageResponse>>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUploadImageSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");

        UploadImageRequest request = new UploadImageRequest();
        request.setImageAlt("kitchen-set-A");
        request.setImagePath("www.example.com");
        request.setProductName("Kitchen Set A");

        Product product = productRepository.findByProductName(request.getProductName());

        mockMvc.perform(
                post("/api/images/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData().getImageId());
            assertEquals(request.getImageAlt(), response.getData().getImageAlt());
            assertEquals(request.getImagePath(), response.getData().getImagePath());
            assertEquals(1, response.getData().getImageStatus());
            assertEquals(product.getProductId(), response.getData().getProductId());
            assertEquals(user.getId(), response.getData().getUserId());
        });
    }

    @Test
    void testDeleteImageUserNotFound() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = productRepository.findByProductName("Kitchen Set A");

        Image image = new Image();
        image.setImageId(UUID.randomUUID().toString());
        image.setImageAlt("kitchen-set-A");
        image.setImagePath("www.example.com");
        image.setImageStatus(1L);
        image.setProduct(product);
        image.setUser(user);
        imageRepository.save(image);

        mockMvc.perform(
                patch("/api/images/" + image.getImageId() + "/users/wrong")
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
    void testDeleteImageForbiddenUser() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("person1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("person1");
        Product product = productRepository.findByProductName("Kitchen Set A");

        Image image = new Image();
        image.setImageId(UUID.randomUUID().toString());
        image.setImageAlt("kitchen-set-A");
        image.setImagePath("www.example.com");
        image.setImageStatus(1L);
        image.setProduct(product);
        image.setUser(user);
        imageRepository.save(image);

        mockMvc.perform(
                patch("/api/images/" + image.getImageId() + "/users/" + user.getId())
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
    void testDeleteImageNotFound() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");

        mockMvc.perform(
                patch("/api/images/wrongImageId/users/" + user.getId())
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
    void testDeleteImageSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = productRepository.findByProductName("Kitchen Set A");

        Image image = new Image();
        image.setImageId(UUID.randomUUID().toString());
        image.setImageAlt("kitchen-set-A");
        image.setImagePath("www.example.com");
        image.setImageStatus(1L);
        image.setProduct(product);
        image.setUser(user);
        imageRepository.save(image);

        mockMvc.perform(
                patch("/api/images/" + image.getImageId() + "/users/" + user.getId())
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
    void testGetImageNotFound() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        mockMvc.perform(
                get("/api/images/wrongImageId")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            GenerateResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetImageSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = productRepository.findByProductName("Kitchen Set A");

        Image image = new Image();
        image.setImageId(UUID.randomUUID().toString());
        image.setImageAlt("kitchen-set-A");
        image.setImagePath("www.example.com");
        image.setImageStatus(1L);
        image.setProduct(product);
        image.setUser(user);
        imageRepository.save(image);

        mockMvc.perform(
                get("/api/images/" + image.getImageId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(image.getImageId(), response.getData().getImageId());
            assertEquals(image.getImageAlt(), response.getData().getImageAlt());
            assertEquals(image.getImagePath(), response.getData().getImagePath());
            assertEquals(image.getImageStatus(), response.getData().getImageStatus());
            assertEquals(image.getProduct().getProductId(), response.getData().getProductId());
            assertEquals(image.getUser().getId(), response.getData().getUserId());
        });
    }

    @Test
    void testGetImageEmptyList() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        mockMvc.perform(
                get("/api/images/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNoContent()
        ).andDo(result -> {
            GenerateResponse<List<ImageResponse>> responses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(responses.getErrors());
        });
    }

    @Test
    void testGetImageListSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = productRepository.findByProductName("Kitchen Set A");

        Image image = new Image();
        for (int i = 0; i < 10; i++) {
            image.setImageId(UUID.randomUUID().toString());
            image.setImageAlt("kitchen-set-" + (i+1));
            image.setImagePath("www.example" + (i+1) + ".com");
            image.setImageStatus(1L);
            image.setProduct(product);
            image.setUser(user);
            imageRepository.save(image);
        }

        mockMvc.perform(
                get("/api/images/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<List<ImageResponse>> responses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(responses.getErrors());
            assertEquals(10, responses.getData().size());
        });
    }

    @Test
    void testGetImagesByPageEmpty() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        mockMvc.perform(
                get("/api/images")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isNoContent()
        ).andDo(result -> {
            GenerateResponse<List<ImageResponse>> responses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(responses.getErrors());
        });
    }

    @Test
    void testGetImagesByPageSuccess() throws Exception{
        UserDetails userDetails = authService.loadUserByUsername("admin1");
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername("admin1");
        Product product = productRepository.findByProductName("Kitchen Set A");

        Image image = new Image();
        for (int i = 0; i < 15; i++) {
            image.setImageId(UUID.randomUUID().toString());
            image.setImageAlt("kitchen-set-" + (i+1));
            image.setImagePath("www.example" + (i+1) + ".com");
            image.setImageStatus(1L);
            image.setProduct(product);
            image.setUser(user);
            imageRepository.save(image);
        }

        for (int i = 0; i < 5; i++) {
            image.setImageId(UUID.randomUUID().toString());
            image.setImageAlt("kitchen-set-" + (i+16));
            image.setImagePath("www.example" + (i+16) + ".com");
            image.setImageStatus(0L);
            image.setProduct(product);
            image.setUser(user);
            imageRepository.save(image);
        }

        mockMvc.perform(
                get("/api/images")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            GenerateResponse<List<ImageResponse>> responses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(responses.getErrors());
            assertEquals(5, responses.getData().size());
            assertEquals(1, responses.getPaging().getCurrentPage());
            assertEquals(2, responses.getPaging().getTotalPage());
            assertEquals(10, responses.getPaging().getSize());
        });
    }
}