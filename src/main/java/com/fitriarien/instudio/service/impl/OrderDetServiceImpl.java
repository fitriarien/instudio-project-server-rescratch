package com.fitriarien.instudio.service.impl;

import com.fitriarien.instudio.entity.Order;
import com.fitriarien.instudio.entity.OrderDetail;
import com.fitriarien.instudio.entity.Product;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.CreateOrderDetRequest;
import com.fitriarien.instudio.model.response.OrderResponse;
import com.fitriarien.instudio.repository.OrderDetRepository;
import com.fitriarien.instudio.repository.OrderRepository;
import com.fitriarien.instudio.repository.ProductRepository;
import com.fitriarien.instudio.repository.UserRepository;
import com.fitriarien.instudio.service.OrderDetService;
import com.fitriarien.instudio.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class OrderDetServiceImpl implements OrderDetService {
    @Autowired
    private OrderDetRepository orderDetRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ValidationService validationService;

    @Override
    @Transactional
    public OrderResponse create(String userId, String orderId, CreateOrderDetRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getStatus() == 0 || user.getRole().equalsIgnoreCase("customer")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to create order detail.");
        }

        Product product = productRepository.findByProductName(request.getProductName());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        order.setOrderAmount(order.getOrderAmount() + request.getProductCost());

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetId(UUID.randomUUID().toString());
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setProductSize(request.getProductSize());
        orderDetail.setProductTheme(request.getProductTheme());
        orderDetail.setTimeEstimation(request.getTimeEstimation());
        orderDetail.setSubtotal(request.getProductCost());

        orderDetRepository.save(orderDetail);
        orderRepository.save(order);
        return orderService.toOrderResponse(order);
    }
}
