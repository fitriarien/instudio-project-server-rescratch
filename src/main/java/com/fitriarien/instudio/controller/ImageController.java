package com.fitriarien.instudio.controller;

import com.fitriarien.instudio.model.request.UploadImageRequest;
import com.fitriarien.instudio.model.response.GenerateResponse;
import com.fitriarien.instudio.model.response.ImageResponse;
import com.fitriarien.instudio.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping(
            path = "/api/images/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<ImageResponse> upload(@PathVariable("userId") String userId,
                                                  @RequestBody UploadImageRequest request) {
        ImageResponse imageResponse = imageService.upload(userId, request);
        return GenerateResponse.<ImageResponse>builder().data(imageResponse).build();
    }

    @PatchMapping(
            path = "/api/images/{imageId}/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateResponse<String> delete(@PathVariable("userId") String userId,
                                           @PathVariable("imageId") String imageId) {
        imageService.delete(imageId, userId);
        return GenerateResponse.<String>builder().data("DELETED").build();
    }
}
