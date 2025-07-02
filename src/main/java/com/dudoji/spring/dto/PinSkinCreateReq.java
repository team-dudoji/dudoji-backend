package com.dudoji.spring.dto;

public record PinSkinCreateReq(
        String name,
        String content,
        String imageUrl,
        int price
) {
}
