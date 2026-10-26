package com.export.car.dto;

public record AuthResponse (
        String token,
        UserResponse user
){
}
