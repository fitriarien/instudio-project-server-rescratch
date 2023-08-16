package com.fitriarien.instudio.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateOrderDetRequest {
    @NotBlank
    private String productName;
    @Range(min = 1)
    private Double productSize;
    @NotBlank
    private String productTheme;
    @Range(min = 1)
    private Double productCost;
    @Range(min = 1)
    private Long timeEstimation;
}
