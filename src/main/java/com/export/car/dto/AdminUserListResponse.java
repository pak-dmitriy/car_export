package com.export.car.dto;

import java.util.List;

public record AdminUserListResponse(
        List<UserResponse> users,
        int page,
        int limit,
        long totalCount
) {
}