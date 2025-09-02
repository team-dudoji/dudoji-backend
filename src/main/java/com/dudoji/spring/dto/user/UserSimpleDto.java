package com.dudoji.spring.dto.user;

import java.time.LocalDate;

import com.dudoji.spring.models.domain.User;

public record UserSimpleDto(
        Long id,
        String name,
        String email,
        String profileImageUrl,
        LocalDate followingAt, // 내가 팔로우 한 날짜
        LocalDate followedAt // 상대가 팔로우한 날짜
) {
    public UserSimpleDto(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileImageUrl(),
                null,
                null

        );
    }
}
