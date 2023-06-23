package com.fitriarien.instudio.service;

import com.fitriarien.instudio.model.request.CreateOrderRequest;
import com.fitriarien.instudio.model.response.OrderResponse;

public interface OrderService {
    OrderResponse create(String userId, CreateOrderRequest request);

}
