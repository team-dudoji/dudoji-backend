package com.dudoji.spring.dto;

import com.dudoji.spring.models.domain.PinSkin;

public record PinSkinSimpleDto(
        String name,
        String content,
        String imageUrl,
        int price
) {
    public PinSkin toDomain() {
        return PinSkin.builder()
                .name(name)
                .content(content)
                .imageUrl(imageUrl)
                .price(price)
                .build();
    }
}
