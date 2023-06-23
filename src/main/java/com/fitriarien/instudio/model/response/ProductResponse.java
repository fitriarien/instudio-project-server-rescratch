package com.fitriarien.instudio.model.response;

import com.fitriarien.instudio.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductResponse {

    private String productId;

    private String productName;

    private String productModel;

    private Double costEstimation;

    private Long productStatus;

    private String userId;
}
