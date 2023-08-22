package com.fitriarien.instudio.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentResponse {
    private String paymentId;
    private String paymentDate;
    private Double paymentAmount;
    private String paymentMethod;
    private String paymentDetail;
    private String accountNumber;
}
