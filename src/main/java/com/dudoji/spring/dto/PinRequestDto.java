package com.dudoji.spring.dto;

import com.dudoji.spring.models.domain.Pin;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PinRequestDto {

    private double lat;
    private double lng;
    private String title;
    private String content;

    public Pin toDomain(Long userId) {
        return Pin.builder()
                .userId(userId)
                .lat(lat)
                .lng(lng)
                .title(title)
                .content(content)
                .build();
    }
}
