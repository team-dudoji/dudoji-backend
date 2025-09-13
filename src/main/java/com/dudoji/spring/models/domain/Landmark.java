package com.dudoji.spring.models.domain;

import com.dudoji.spring.dto.landmark.LandmarkRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

    @Setter
    private Festival festival;

    public Landmark(Long landmarkId, double lat, double lng, String content, String mapImageUrl, String detailImageUrl, String placeName, String address, boolean isDetected) {
        this(landmarkId, lat, lng, content, mapImageUrl, detailImageUrl,  placeName, address, isDetected, null);
    }

    public Landmark(Long landmarkId, LandmarkRequestDto landmarkRequestDto) {
        this(landmarkId,
                landmarkRequestDto.lat(),
                landmarkRequestDto.lng(),
                landmarkRequestDto.content(),
                landmarkRequestDto.mapImageUrl(),
                landmarkRequestDto.detailImageUrl(),
                landmarkRequestDto.placeName(),
                landmarkRequestDto.address(),
                false,
                null
        );
    }
}
