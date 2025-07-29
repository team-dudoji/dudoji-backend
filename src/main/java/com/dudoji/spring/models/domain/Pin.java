package com.dudoji.spring.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Pin {
    private double lat;
    private double lng;
    private Long pinId;
    private Long userId;
    private LocalDateTime createdDate;
    private String content;
    private String imageUrl;
    private String placeName;
    private String address;
    private Long pinSkinId;
    private int likes;
}
