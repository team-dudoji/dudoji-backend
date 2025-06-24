package com.dudoji.spring.dto;

import com.dudoji.spring.models.domain.Pin;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
                .build();
    }
}
