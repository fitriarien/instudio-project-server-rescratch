package com.fitriarien.instudio.service;

import com.fitriarien.instudio.model.request.CreatePaymentRequest;
import com.fitriarien.instudio.model.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse create(String userId, String orderId, CreatePaymentRequest request);
}
