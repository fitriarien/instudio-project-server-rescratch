package com.fitriarien.instudio.service;

import com.fitriarien.instudio.model.request.CreateOrderRequest;
import com.fitriarien.instudio.model.response.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    OrderResponse create(String userId, CreateOrderRequest request);
    List<OrderResponse> getOrderByUser(String userId);
    OrderResponse get(String userId, String orderId);
    List<OrderResponse> getOrders(String userId);
    Page<OrderResponse> getOrdersByPage(String userId, int page, int size);
}
