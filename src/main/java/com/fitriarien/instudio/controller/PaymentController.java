package com.fitriarien.instudio.controller;

import com.fitriarien.instudio.model.request.CreatePaymentRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.PaymentResponse;
import com.fitriarien.instudio.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping(
            path = "/api/payments/orders/{orderId}/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<PaymentResponse> create(@PathVariable("userId") String userId,
                                                    @PathVariable("orderId") String orderId,
                                                    @RequestBody CreatePaymentRequest request) {
        PaymentResponse paymentResponse = paymentService.create(userId, orderId, request);
        return GenerateResponse.<PaymentResponse>builder().data(paymentResponse).build();
    }
}
