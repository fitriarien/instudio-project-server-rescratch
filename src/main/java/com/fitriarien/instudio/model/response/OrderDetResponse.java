package com.fitriarien.instudio.model.response;

import com.fitriarien.instudio.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDetResponse {
    private String orderDetId;
    private Double productSize;
    private String productTheme;
    private Long timeEstimation;
    private Double subtotal;
    private Product product;
}
