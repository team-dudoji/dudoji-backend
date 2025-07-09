package com.dudoji.spring.dto.skin;

import com.dudoji.spring.models.domain.skin.CharacterSkin;
import com.dudoji.spring.models.domain.skin.PinSkin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CharacterSkinDto {
	private long skinId;
	private String name;
	private String content;
	private String imageUrl;
	private int price;
	private boolean isPurchased;

	public CharacterSkin toDomain() {
		return CharacterSkin.builder()
			.skinId(skinId)
			.name(name)
			.content(content)
			.imageUrl(imageUrl)
			.price(price)
			.build();
	}
}
