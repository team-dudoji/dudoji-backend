package com.dudoji.spring.dto.user;

import com.dudoji.spring.models.domain.User;

public record UserSimpleDto(
        Long id,
        String name,
        String email,
        String profileImageUrl
) {
    public UserSimpleDto(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileImageUrl()
        );
    }
}
