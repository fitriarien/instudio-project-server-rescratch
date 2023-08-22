package com.fitriarien.instudio.service.impl;

import com.fitriarien.instudio.entity.Order;
import com.fitriarien.instudio.entity.Payment;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.CreatePaymentRequest;
import com.fitriarien.instudio.model.response.OrderResponse;
import com.fitriarien.instudio.model.response.PaymentResponse;
import com.fitriarien.instudio.repository.OrderRepository;
import com.fitriarien.instudio.repository.PaymentRepository;
import com.fitriarien.instudio.repository.UserRepository;
import com.fitriarien.instudio.service.PaymentService;
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
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ValidationService validationService;

    @Override
    @Transactional
    public PaymentResponse create(String userId, String orderId, CreatePaymentRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getRole().equalsIgnoreCase("customer") || user.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to order.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setPaymentDate(handlePaymentDate());
        payment.setPaymentAmount(request.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDetail(request.getPaymentDetail());
        payment.setAccountNumber(request.getAccountNumber());
        payment.setOrder(order);

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    private String handlePaymentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.now();
        return formatter.format(currentDateTime);
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .paymentDate(payment.getPaymentDate())
                .paymentAmount(payment.getPaymentAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDetail(payment.getPaymentDetail())
                .accountNumber(payment.getAccountNumber())
                .build();
    }
}
