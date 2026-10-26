package com.export.car.dto;

import com.export.car.entity.Role;
import com.export.car.entity.User;
import com.export.car.entity.UserStatus;

public record UserResponse (
        Long id,
        String fullName,
        String email,
        Role role,
        UserStatus status,
        String preferredLanguage
){
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getPreferredLanguage()
        );
    }
}
