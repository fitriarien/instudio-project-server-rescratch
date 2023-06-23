package com.fitriarien.instudio.service.impl;

import com.fitriarien.instudio.entity.Order;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.CreateOrderRequest;
import com.fitriarien.instudio.model.response.OrderResponse;
import com.fitriarien.instudio.model.response.UserResponse;
import com.fitriarien.instudio.repository.OrderRepository;
import com.fitriarien.instudio.repository.UserRepository;
import com.fitriarien.instudio.service.OrderService;
import com.fitriarien.instudio.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ValidationService validationService;

    @Override
    @Transactional
    public OrderResponse create(String userId, CreateOrderRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getRole().equalsIgnoreCase("customer") || user.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to order.");
        }

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderCode(handleOrderCode());
        order.setOrderDate(handleOrderDate());
        order.setVisitSchedule(request.getVisitSchedule());
        order.setVisitAddress(request.getVisitAddress());
        order.setOrderAmount(0D);
        order.setOrderStatus(0L);
        order.setUser(user);

        orderRepository.save(order);
        return toOrderResponse(order);
    }

    private String handleOrderCode() {
        return "TR" + (orderRepository.getMaxOrder()+1);
    }

    private String handleOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.now();
        return formatter.format(currentDateTime);
    }

    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .orderDate(order.getOrderDate())
                .visitSchedule(order.getVisitSchedule())
                .visitAddress(order.getVisitAddress())
                .orderAmount(order.getOrderAmount())
                .orderStatus(order.getOrderStatus())
                .orderDetailList(order.getOrderDetailList())
                .build();
    }
}
