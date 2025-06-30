package com.dudoji.spring.dto.landmark;

public record LandmarkResponseDto(
        double lat,
        double lng,
        String placeName,
        String address,
        String content,
        String imageUrl,
        boolean detected
) {}
