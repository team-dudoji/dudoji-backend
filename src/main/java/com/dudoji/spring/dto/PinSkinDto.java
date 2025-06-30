package com.dudoji.spring.dto;

import com.dudoji.spring.models.domain.PinSkin;
import lombok.Data;

@Data
public class PinSkinDto {
    private long skinId;
    private String name;
    private String content;
    private String imageUrl;
    private int price;
    private boolean isPurchased;

    public PinSkinDto(PinSkin pinSkin, boolean isPurchased) {
        this.skinId = pinSkin.getSkinId();
        this.name = pinSkin.getName();
        this.content = pinSkin.getContent();
        this.imageUrl = pinSkin.getImageUrl();
        this.price = pinSkin.getPrice();
        this.isPurchased = isPurchased;
    }

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
