package com.fitriarien.instudio.service;

import com.fitriarien.instudio.model.request.UploadImageRequest;
import com.fitriarien.instudio.model.response.ImageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ImageService {
    ImageResponse upload(String userId, UploadImageRequest request);

    void delete(String imageId, String userId);

    ImageResponse get(String imageId);

    List<ImageResponse> getList();

    Page<ImageResponse> getByPage();
}
