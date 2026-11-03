package com.export.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @NotBlank
        @Size(min = 2, max = 100)
        String fullName,

        @Pattern(regexp = "en|ru", message = "Language must be en or ru")
        String preferredLanguage
) {
}
