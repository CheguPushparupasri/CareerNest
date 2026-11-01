package com.careernest.modules.user.service;

import com.careernest.modules.user.dto.UserResponse;
import com.careernest.modules.user.entity.User;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        Hibernate.initialize(user.getRole());
        if (user.getProfile() != null) {
            Hibernate.initialize(user.getProfile());
        }
        return new UserResponse(
                user.getPublicId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName().name(),
                user.isEmailVerified(),
                user.isProfileSetupCompleted(),
                user.getAccountStatus().name()
        );
    }
}
