package com.fitriarien.instudio.service.impl;

import com.fitriarien.instudio.entity.Image;
import com.fitriarien.instudio.entity.Product;
import com.fitriarien.instudio.entity.User;
import com.fitriarien.instudio.model.request.UploadImageRequest;
import com.fitriarien.instudio.model.response.ImageResponse;
import com.fitriarien.instudio.repository.ImageRepository;
import com.fitriarien.instudio.repository.ProductRepository;
import com.fitriarien.instudio.repository.UserRepository;
import com.fitriarien.instudio.service.ImageService;
import com.fitriarien.instudio.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Override
    @Transactional
    public ImageResponse upload(String userId, UploadImageRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unauthorized"));

        if (!user.getRole().equalsIgnoreCase("admin") && user.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to upload image.");
        }

        Product product = productRepository.findByProductName(request.getProductName());
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        Image image = new Image();
        image.setImageId(UUID.randomUUID().toString());
        image.setImageAlt(request.getImageAlt());
        image.setImagePath(request.getImagePath());
        image.setImageStatus(1L);
        image.setProduct(product);
        image.setUser(user);

        imageRepository.save(image);
        return toImageResponse(image);
    }

    @Override
    @Transactional
    public void delete(String imageId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unauthorized"));
        if (!user.getRole().equalsIgnoreCase("admin") && user.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to upload image.");
        }

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        image.setImageStatus(0L);
        imageRepository.save(image);
    }

    @Override
    @Transactional(readOnly = true)
    public ImageResponse get(String imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        return toImageResponse(image);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageResponse> getList() {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ImageResponse> getByPage() {
        return null;
    }

    private ImageResponse toImageResponse(Image image) {
        return ImageResponse.builder()
                .imageId(image.getImageId())
                .imageAlt(image.getImageAlt())
                .imagePath(image.getImagePath())
                .imageStatus(image.getImageStatus())
                .productId(image.getProduct().getProductId())
                .userId(image.getUser().getId())
                .build();
    }

}
