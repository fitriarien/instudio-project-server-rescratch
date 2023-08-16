package com.fitriarien.instudio.controller;

import com.fitriarien.instudio.model.request.CreateOrderRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.OrderResponse;
import com.fitriarien.instudio.model.response.PagingResponse;
import com.fitriarien.instudio.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(
            path = "/api/orders/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<OrderResponse> create(@PathVariable("userId") String userId,
                                                  @RequestBody CreateOrderRequest request) {
        OrderResponse orderResponse = orderService.create(userId, request);
        return GenerateResponse.<OrderResponse>builder().data(orderResponse).build();
    }

    @GetMapping(
            path = "/api/orders/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<List<OrderResponse>> getOrderByUser(@PathVariable("userId") String userId) {
        List<OrderResponse> orderResponses = orderService.getOrderByUser(userId);
        return GenerateResponse.<List<OrderResponse>>builder().data(orderResponses).build();
    }

    @GetMapping(
            path = "/api/orders/{orderId}/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<OrderResponse> get(@PathVariable("userId") String userId,
                                               @PathVariable("orderId") String orderId) {
        OrderResponse orderResponse = orderService.get(userId, orderId);
        return GenerateResponse.<OrderResponse>builder().data(orderResponse).build();
    }

    @GetMapping(
            path = "/api/orders/all/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<List<OrderResponse>> getOrders(@PathVariable("userId") String userId) {
        List<OrderResponse> orderResponses = orderService.getOrders(userId);
        return GenerateResponse.<List<OrderResponse>>builder().data(orderResponses).build();
    }

    @GetMapping(
            path = "/api/orders/pageable/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<List<OrderResponse>> getOrdersByPage(@PathVariable("userId") String userId,
                                                                 @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                 @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<OrderResponse> orderResponses = orderService.getOrdersByPage(userId, page, size);
        return GenerateResponse.<List<OrderResponse>>builder()
                .data(orderResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(orderResponses.getNumber())
                        .totalPage(orderResponses.getTotalPages())
                        .size(orderResponses.getSize())
                        .build())
                .build();
    }
}
