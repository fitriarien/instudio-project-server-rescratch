package com.fitriarien.instudio.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ImageResponse {

    private String imageId;

    private String imageAlt;

    private String imagePath;

    private Long imageStatus;

    private String productId;

    private String userId;
}
