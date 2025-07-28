package com.dudoji.spring.models.domain;

import com.dudoji.spring.dto.landmark.LandmarkRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Landmark {
    private long landmarkId;
    private double lat;
    private double lng;
    private String content;
    private String mapImageUrl;
    private String detailImageUrl;
    private String placeName;
    private String address;
    private boolean isDetected;

    public Landmark(Long landmarkId, LandmarkRequestDto landmarkRequestDto) {
        this(landmarkId,
                landmarkRequestDto.lat(),
                landmarkRequestDto.lng(),
                landmarkRequestDto.content(),
                landmarkRequestDto.mapImageUrl(),
                landmarkRequestDto.detailImageUrl(),
                landmarkRequestDto.placeName(),
                landmarkRequestDto.address(),
                false
        );
    }
}
