package com.fitriarien.instudio.controller;

import com.fitriarien.instudio.model.request.CreateProductRequest;
import com.fitriarien.instudio.model.request.UpdateProductRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.PagingResponse;
import com.fitriarien.instudio.model.response.ProductResponse;
import com.fitriarien.instudio.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping(
            path = "/api/products/list",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<List<ProductResponse>> getList() {
        List<ProductResponse> productResponseList = productService.getList();
        return GenerateResponse.<List<ProductResponse>>builder().data(productResponseList).build();
    }

    @GetMapping(
            path = "/api/products/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<ProductResponse> get(@PathVariable("productId") String id) {
        ProductResponse productResponse = productService.get(id);
        return GenerateResponse.<ProductResponse>builder().data(productResponse).build();
    }

    @PostMapping(
            path = "/api/products/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<ProductResponse> create(@PathVariable("userId") String id,
                                                    @RequestBody CreateProductRequest request) {
        ProductResponse productResponse = productService.create(id, request);
        return GenerateResponse.<ProductResponse>builder().data(productResponse).build();
    }

    @PutMapping(
            path = "/api/products/{productId}/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<ProductResponse> update(@PathVariable("productId") String productId,
                                                    @PathVariable("userId") String userId,
                                                    @RequestBody UpdateProductRequest request) {
        ProductResponse productResponse = productService.update(productId, userId, request);
        return GenerateResponse.<ProductResponse>builder().data(productResponse).build();
    }

    @PatchMapping(
            path = "/api/products/{productId}/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<String> delete(@PathVariable("productId") String productId,
                                           @PathVariable("userId") String userId) {
        productService.delete(productId, userId);
        return GenerateResponse.<String>builder().data("DELETED").build();
    }

    @GetMapping(
            path = "/api/products",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<List<ProductResponse>> getByPage(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<ProductResponse> productResponsePage = productService.getByPage(page, size);
        return GenerateResponse.<List<ProductResponse>>builder()
                .data(productResponsePage.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(productResponsePage.getNumber())
                        .totalPage(productResponsePage.getTotalPages())
                        .size(productResponsePage.getSize())
                        .build())
                .build();
    }
}
