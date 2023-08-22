package com.fitriarien.instudio.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreatePaymentRequest {
    @Range(min = 1)
    private Double paymentAmount;
    @NotBlank
    private String paymentMethod;
    @NotBlank
    private String paymentDetail;
    private String accountNumber;
}
