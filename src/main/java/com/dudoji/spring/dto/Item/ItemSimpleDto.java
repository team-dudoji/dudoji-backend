package com.dudoji.spring.dto.Item;

import com.dudoji.spring.models.domain.Item;

public record ItemSimpleDto (
	long itemId,
	String name,
	String content,
	String imageUrl,
	int price,
	long stock,
	long userId
) {
	public ItemSimpleDto(Item item) {
		this (
			item.getItemId(),
			item.getName(),
			item.getContent(),
			item.getImageUrl(),
			item.getPrice(),
			0,
			-1
		);
	}

	public ItemSimpleDto(Item item, long userId, long stock) {
		this (
			item.getItemId(),
			item.getName(),
			item.getContent(),
			item.getImageUrl(),
			item.getPrice(),
			stock,
			userId
		);
	}

	public Item toDomain() {
		return Item.builder()
			.itemId(itemId)
			.name(name)
			.content(content)
			.price(price)
			.imageUrl(imageUrl)
			.build();
	}
}
