package com.dudoji.spring.dto;

import com.dudoji.spring.models.domain.Pin;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PinResponseDto {

    // SAME WITH PIN
    private double lat;
    private double lng;
    private Long userId;
    private LocalDateTime createdDate;
    private String title;
    private String content;
    // ONLY IN DTO
    private Who master;

    public enum Who {
        MINE,
        FOLLOWING,
        UNKNOWN
    }

    public PinResponseDto(Pin pin) {
        this.lat = pin.getLat();
        this.lng = pin.getLng();
        this.userId = pin.getUserId();
        this.createdDate = pin.getCreatedDate();
        this.title = pin.getTitle();
        this.content = pin.getContent();
    }
}
