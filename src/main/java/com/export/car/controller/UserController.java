package com.export.car.controller;

import com.export.car.dto.UpdateProfileRequest;
import com.export.car.dto.UserResponse;
import com.export.car.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(Authentication auth) {
        return userService.getProfile(auth.getName());
    }

    @PutMapping("/me")
    public UserResponse update(Authentication auth, @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(auth.getName(), request);
    }
}