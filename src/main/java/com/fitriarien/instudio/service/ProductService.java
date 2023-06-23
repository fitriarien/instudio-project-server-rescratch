package com.fitriarien.instudio.service;

import com.fitriarien.instudio.model.request.CreateProductRequest;
import com.fitriarien.instudio.model.request.UpdateProductRequest;
import com.fitriarien.instudio.model.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getList();

    ProductResponse get(String productId);

    ProductResponse create(String userId, CreateProductRequest request);

    ProductResponse update(String productId, String userId, UpdateProductRequest request);

    void delete(String productId, String userId);

    Page<ProductResponse> getByPage(int page, int size);
}
