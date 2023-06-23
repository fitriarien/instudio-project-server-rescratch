package com.fitriarien.instudio.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateOrderRequest {

    @NotBlank
    private String visitSchedule;

    @NotBlank
    @Size(max = 200)
    private String visitAddress;
}
