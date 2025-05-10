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
    private String title;
    private String content;
    private LocalDateTime createdDate;

    public Pin toDomain(Long userId) {
        return Pin.builder()
                .userId(userId)
                .lat(lat)
                .lng(lng)
                .title(title)
                .content(content)
                .createdDate(createdDate)
                .build();
    }
}
