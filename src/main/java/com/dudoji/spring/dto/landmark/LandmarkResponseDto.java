package com.dudoji.spring.dto.landmark;

import com.dudoji.spring.config.LandmarkConfig;
import com.dudoji.spring.models.domain.Landmark;

public record LandmarkResponseDto(
        long landmarkId,
        double lat,
        double lng,
        String placeName,
        String address,
        String content,
        String imageUrl,
        double radius,
        boolean isDetected
) {
    public LandmarkResponseDto(Landmark landmark) {
        this(
            landmark.getLandmarkId(),
            landmark.getLat(),
            landmark.getLng(),
            landmark.getPlaceName(),
            landmark.getAddress(),
            landmark.getContent(),
            landmark.getImageUrl(), landmark.isDetected() ? LandmarkConfig.LANDMARK_DETECTED_RADIUS : LandmarkConfig.LANDMARK_UNDETECTED_RADIUS,
            landmark.isDetected());
        }
}
