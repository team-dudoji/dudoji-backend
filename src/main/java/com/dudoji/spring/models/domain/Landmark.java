package com.dudoji.spring.models.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Landmark {
    private long landmarkId;
    private double lat;
    private double lng;
    private String content;
    private String imageUrl;
    private String placeName;
    private String address;
    private boolean isDetected;
}
