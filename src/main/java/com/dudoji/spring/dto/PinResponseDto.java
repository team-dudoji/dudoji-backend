package com.dudoji.spring.dto;

import com.dudoji.spring.models.domain.Pin;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PinResponseDto {

    // SAME WITH PIN
    private Long pinId;
    private double lat;
    private double lng;
    private Long userId;
    private LocalDateTime createdDate;
    private String content;
    private Who master;
    private int likeCount;
    private boolean isLiked;
    private String imageUrl;
    private String placeName;
    private String address;

    public enum Who {
        MINE,
        FOLLOWING,
        UNKNOWN;
    }

    public PinResponseDto(Pin pin) {
        this.pinId = pin.getPinId();
        this.lat = pin.getLat();
        this.lng = pin.getLng();
        this.userId = pin.getUserId();
        this.createdDate = pin.getCreatedDate();
        this.content = pin.getContent();
        this.imageUrl = pin.getImageUrl();
        this.placeName = pin.getPlaceName();
        this.address = pin.getAddress();
    }
}
