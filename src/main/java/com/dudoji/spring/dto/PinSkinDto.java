package com.dudoji.spring.dto;

import com.dudoji.spring.models.domain.PinSkin;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PinSkinDto {
    private long skinId;
    private String name;
    private String content;
    private String imageUrl;
    private int price;
    private boolean isPurchased;

    public PinSkin toDomain() {
        return PinSkin.builder()
                .skinId(skinId)
                .name(name)
                .content(content)
                .imageUrl(imageUrl)
                .price(price)
                .build();
    }
}
