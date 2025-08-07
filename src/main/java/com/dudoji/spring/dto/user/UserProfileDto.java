package com.dudoji.spring.dto.user;

import com.dudoji.spring.models.domain.User;

public record UserProfileDto(
        long userId,
        String name,
        String profileImageUrl,
        String email,
        int pinCount,
        int followerCount,
        int followingCount
) {
    public UserProfileDto(
            User user,
            int pinCount,
            int followerCount,
            int followingCount) {
        this(
                user.getId(),
                user.getName(),
                user.getProfileImageUrl(),
                user.getEmail(),
                pinCount,
                followerCount,
                followingCount
        );
    }
}