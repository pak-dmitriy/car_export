package com.export.car.controller;

import com.export.car.dto.AdminUpdateUserRequest;
import com.export.car.dto.AdminUserListResponse;
import com.export.car.dto.UserResponse;
import com.export.car.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public AdminUserListResponse list(@RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int limit) {
        return userService.getUsers(keyword, page, limit);
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody AdminUpdateUserRequest request) {
        return userService.updateByAdmin(id, request);
    }
}