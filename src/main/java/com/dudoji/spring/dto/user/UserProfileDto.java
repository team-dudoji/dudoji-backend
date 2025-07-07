package com.dudoji.spring.dto.user;

import com.dudoji.spring.models.domain.User;

public record UserProfileDto(
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
                user.getName(),
                user.getProfileImageUrl(),
                user.getEmail(),
                pinCount,
                followerCount,
                followingCount
        );
    }
}