package com.dudoji.spring.models.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Pin {

    private double lat;
    private double lng;
    private Long pinId;
    private Long userId;
    private Date createdDate;
    private String title;
    private String content;
}
