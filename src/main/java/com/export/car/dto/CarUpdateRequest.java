package com.export.car.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CarUpdateRequest(

        @NotBlank
        @Size(min = 3, max = 150)
        String title,

        @NotBlank
        String brand,

        @NotBlank
        String model,

        @NotNull
        @Min(1980)
        Integer year,

        @NotNull
        @Positive
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer mileage,

        @NotBlank
        String fuelType,

        @NotBlank
        String transmission,

        @Positive
        BigDecimal engineVolume,

        @NotBlank
        String bodyType,

        String color,

        String countryOfOrigin,

        String exportLocation,

        @Size(max = 5000)
        String description

) {

}
