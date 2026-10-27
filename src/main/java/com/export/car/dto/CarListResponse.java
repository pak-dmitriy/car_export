package com.export.car.dto;

import java.util.List;

public record CarListResponse(
        List<CarResponse> items,
        int page,
        int limit,
        long totalCount
) {
}
