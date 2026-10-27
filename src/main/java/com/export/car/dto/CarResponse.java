package com.export.car.dto;

import com.export.car.entity.Car;
import com.export.car.entity.CarStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CarResponse (

        Long id,
        String title,
        String brand,
        String model,
        Integer year,
        BigDecimal price,
        Integer mileage,
        String fuelType,
        String transmission,
        BigDecimal engineVolume,
        String bodyType,
        String color,
        String countryOfOrigin,
        String exportLocation,
        String description,
        CarStatus status,
        List<ImageResponse> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
    public record ImageResponse(Long id, String fileUrl, boolean main) {

    }

    public static CarResponse from(Car car) {
        List<ImageResponse> images = car.getImages().stream()
                .map(i -> new ImageResponse(i.getId(), i.getFileUrl(), i.isMain()))
                .toList();

        return new CarResponse(
                car.getId(), car.getTitle(), car.getBrand(), car.getModel(),
                car.getYear(), car.getPrice(), car.getMileage(),
                car.getFuelType(), car.getTransmission(), car.getEngineVolume(),
                car.getBodyType(), car.getColor(), car.getCountryOfOrigin(),
                car.getExportLocation(), car.getDescription(), car.getStatus(),
                images, car.getCreatedAt(), car.getUpdatedAt()
        );
    }
}
