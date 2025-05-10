package com.dudoji.spring.models.domain;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Pin {

    private double lat;
    private double lng;
    private Long pinId;
    private Long userId;
    private LocalDateTime createdDate;
    private String title;
    private String content;
}
