package com.fitriarien.instudio.service.impl;

import com.fitriarien.instudio.entity.Product;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.CreateProductRequest;
import com.fitriarien.instudio.model.request.UpdateProductRequest;
import com.fitriarien.instudio.model.response.ProductResponse;
import com.fitriarien.instudio.model.response.UserResponse;
import com.fitriarien.instudio.repository.ProductRepository;
import com.fitriarien.instudio.repository.UserRepository;
import com.fitriarien.instudio.service.ProductService;
import com.fitriarien.instudio.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Predicates;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public List<ProductResponse> getList() {
        List<Product> productList = productRepository.findAll();

        if (productList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Data is empty.");
        }

        return productList.stream()
                .filter(product -> product.getProductStatus() != 0)
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ProductResponse get(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        return toProductResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse create(String userId, CreateProductRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unauthorized"));

        if (!user.getRole().equalsIgnoreCase("admin") && user.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to create product.");
        }

        log.info("Creating product...");

        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName(request.getProductName());
        product.setProductModel(request.getProductModel());
        product.setCostEstimation(request.getCostEstimation());
        product.setProductStatus(1L);
        product.setUser(user);

        productRepository.save(product);
        return toProductResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse update(String productId, String userId, UpdateProductRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!user.getRole().equalsIgnoreCase("admin") && user.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to update product.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
        }
        if (request.getProductModel() != null) {
            product.setProductModel(request.getProductModel());
        }
        if (request.getCostEstimation() != null) {
            product.setCostEstimation(request.getCostEstimation());
        }

        productRepository.save(product);
        return toProductResponse(product);
    }

    @Transactional
    @Override
    public void delete(String productId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!user.getRole().equalsIgnoreCase("admin") && user.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to delete product.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        product.setProductStatus(0L);
        productRepository.save(product);
    }

    @Transactional
    @Override
    public Page<ProductResponse> getByPage(int page, int size) {
        Specification<Product> specification = Specification.where((root, query, criteriaBuilder) ->
            criteriaBuilder.notEqual(root.get("productStatus"), 0));

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(specification, pageable);

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Data is empty.");
        }

        List<ProductResponse> responseList = products.getContent().stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, products.getTotalElements());
    }

    protected ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productModel(product.getProductModel())
                .costEstimation(product.getCostEstimation())
                .productStatus(product.getProductStatus())
                .userId(product.getUser().getId())
                .build();
    }
}
