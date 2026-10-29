package com.export.car.controller;

import com.export.car.dto.CarResponse;
import com.export.car.service.CarImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/cars/{carId}/images")
@RequiredArgsConstructor
public class CarImageController {

    private final CarImageService carImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponse.ImageResponse upload(@PathVariable Long carId,
                                            @RequestParam("file") MultipartFile file) {
        return carImageService.upload(carId, file);
    }

    @PatchMapping("/{imageId}/main")
    public CarResponse setMain(@PathVariable Long carId, @PathVariable Long imageId) {
        return carImageService.setMain(carId, imageId);
    }

    @DeleteMapping("/{imageId}")
    public CarResponse delete(@PathVariable Long carId, @PathVariable Long imageId) {
        return carImageService.delete(carId, imageId);
    }
}