package com.export.car.dto;

import com.export.car.entity.Role;
import com.export.car.entity.UserStatus;

public record AdminUpdateUserRequest(
        Role role,
        UserStatus status
) {
}
