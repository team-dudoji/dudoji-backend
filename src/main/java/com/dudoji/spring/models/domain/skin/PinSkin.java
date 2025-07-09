package com.dudoji.spring.models.domain.skin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PinSkin {
    private long skinId;
    private String name;
    private String content;
    private String imageUrl;
    private int price;
}
