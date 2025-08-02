package com.dudoji.spring.dto.pin;

import com.dudoji.spring.models.domain.Pin;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PinRequestDto {

    private double lat;
    private double lng;
    private String content;
    private LocalDateTime createdDate;
    private String imageUrl;
    private String placeName;
    private String address;
    private Long pinSkinId;
    private List<String> hashtags;

    public Pin toDomain(Long userId) {
        return Pin.builder()
                .userId(userId)
                .lat(lat)
                .lng(lng)
                .content(content)
                .createdDate(createdDate)
                .imageUrl(imageUrl)
                .placeName(placeName)
                .address(address)
                .pinSkinId(pinSkinId)
                .build();
    }
}
