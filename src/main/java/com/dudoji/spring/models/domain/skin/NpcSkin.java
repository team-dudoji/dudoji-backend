package com.dudoji.spring.models.domain.skin;

import com.dudoji.spring.dto.npc.NpcSkinDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NpcSkin {
	private long npcSkinId;
	private String imageUrl;
	private int regionId;

	public NpcSkin(NpcSkinDto dto) {
		this.npcSkinId = 0;
		this.imageUrl = dto.imageUrl();
		this.regionId = dto.regionId();
	}
}
