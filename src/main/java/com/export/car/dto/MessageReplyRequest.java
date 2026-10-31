package com.export.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageReplyRequest(
        @NotBlank
        @Size(max = 2000)
        String text
) {
}