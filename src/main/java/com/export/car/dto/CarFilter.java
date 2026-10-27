package com.export.car.dto;

import com.export.car.entity.CarStatus;

import java.math.BigDecimal;

public record CarFilter(
        String keyword,
        String brand,
        String model,
        Integer yearFrom,
        Integer yearTo,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        String fuelType,
        String transmission,
        String bodyType,
        CarStatus status
) {
}
