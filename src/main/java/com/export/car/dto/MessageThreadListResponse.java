package com.export.car.dto;

import java.util.List;

public record MessageThreadListResponse(
        List<MessageThreadResponse> items,
        int page,
        int limit,
        long totalCount
) {
}