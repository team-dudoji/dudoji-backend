package com.dudoji.spring.models.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PinSkin {
    private long skinId;
    private String name;
    private String content;
    private String imageUrl;
    private int price; // TODO: 필요한 가?
}
