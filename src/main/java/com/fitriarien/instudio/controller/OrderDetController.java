package com.fitriarien.instudio.controller;

import com.fitriarien.instudio.model.request.CreateOrderDetRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.OrderResponse;
import com.fitriarien.instudio.service.OrderDetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class OrderDetController {
    @Autowired
    private OrderDetService orderDetService;

    @PostMapping(
            path = "/api/orders/{orderId}/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<OrderResponse> create(@PathVariable("userId") String userId,
                                                  @PathVariable("orderId") String orderId,
                                                  @RequestBody CreateOrderDetRequest request) {
        OrderResponse response = orderDetService.create(userId, orderId, request);
        return GenerateResponse.<OrderResponse>builder().data(response).build();
    }
}
