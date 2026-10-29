package com.export.car.dto;

import com.export.car.entity.CarStatus;
import jakarta.validation.constraints.NotNull;

public record CarStatusRequest (
        @NotNull
        CarStatus status
){
}
