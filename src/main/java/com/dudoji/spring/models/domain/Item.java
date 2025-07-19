package com.dudoji.spring.models.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
	private long itemId;
	private String name;
	private String content;
	private String imageUrl;
	private int price;
}
