package com.fitriarien.instudio.service;

import com.fitriarien.instudio.model.request.CreateOrderDetRequest;
import com.fitriarien.instudio.model.response.OrderResponse;

public interface OrderDetService {
    OrderResponse create(String userId, String orderId, CreateOrderDetRequest request);
}
