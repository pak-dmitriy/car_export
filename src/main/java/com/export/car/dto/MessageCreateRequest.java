package com.export.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageCreateRequest(

        Long carId,

        @NotBlank
        @Size(min = 3, max = 150)
        String subject,

        @NotBlank
        @Size(max = 2000)
        String text
) {
}