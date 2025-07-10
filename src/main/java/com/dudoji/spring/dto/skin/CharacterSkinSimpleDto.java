package com.dudoji.spring.dto.skin;

import com.dudoji.spring.models.domain.skin.CharacterSkin;

public record CharacterSkinSimpleDto(
	String name,
	String content,
	String imageUrl,
	int price
) {
	public CharacterSkin toDomain() {
		return CharacterSkin.builder()
			.name(name)
			.content(content)
			.imageUrl(imageUrl)
			.price(price)
			.build();
	}
}
