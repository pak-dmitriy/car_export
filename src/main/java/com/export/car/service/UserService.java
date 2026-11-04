package com.export.car.service;

import com.export.car.dto.AdminUpdateUserRequest;
import com.export.car.dto.AdminUserListResponse;
import com.export.car.dto.UpdateProfileRequest;
import com.export.car.dto.UserResponse;
import com.export.car.entity.Role;
import com.export.car.entity.User;
import com.export.car.entity.UserStatus;
import com.export.car.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {
        return UserResponse.from(findUser(email));
    }

    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findUser(email);
        user.setFullName(request.fullName().trim());
        if (request.preferredLanguage() != null) {
            user.setPreferredLanguage(request.preferredLanguage());
        }
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public AdminUserListResponse getUsers(String keyword, int page, int limit) {
        if (page < 1 || limit < 1 || limit > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "page must be >= 1, limit must be between 1 and 50");
        }

        Pageable pageable = PageRequest.of(page - 1, limit,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id")));

        Page<User> result;
        if (keyword == null || keyword.isBlank()) {
            result = userRepository.findAll(pageable);
        } else {
            String word = keyword.trim();
            result = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    word, word, pageable);
        }

        List<UserResponse> users = result.getContent().stream().map(UserResponse::from).toList();
        return new AdminUserListResponse(users, page, limit, result.getTotalElements());
    }

    @Transactional
    public UserResponse updateByAdmin(Long id, AdminUpdateUserRequest request) {
        if (request.role() == null && request.status() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role or status is required");
        }

        User target = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Role newRole = request.role() != null ? request.role() : target.getRole();
        UserStatus newStatus = request.status() != null ? request.status() : target.getStatus();

        boolean wasActiveAdmin = target.getRole() == Role.ADMIN && target.getStatus() == UserStatus.ACTIVE;
        boolean willBeActiveAdmin = newRole == Role.ADMIN && newStatus == UserStatus.ACTIVE;

        if (wasActiveAdmin && !willBeActiveAdmin
                && userRepository.countByRoleAndStatus(Role.ADMIN, UserStatus.ACTIVE) <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot remove the last active administrator");
        }

        target.setRole(newRole);
        target.setStatus(newStatus);
        return UserResponse.from(target);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
    }
}