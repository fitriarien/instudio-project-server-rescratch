package com.fitriarien.instudio.controller;

import com.fitriarien.instudio.model.request.CreateOrderRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.OrderResponse;
import com.fitriarien.instudio.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
}
