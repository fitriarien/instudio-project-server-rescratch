package com.fitriarien.instudio.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateProductRequest {

    @NotBlank
    @Size(max = 100)
    private String productName;

    @Size(max = 100)
    private String productModel;

    private Double costEstimation;
}
