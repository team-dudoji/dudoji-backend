package com.dudoji.spring.dto.landmark;

public record LandmarkRequestDto(
        double lat,
        double lng,
        String placeName,
        String address,
        String content,
        String imageUrl
) {
}
