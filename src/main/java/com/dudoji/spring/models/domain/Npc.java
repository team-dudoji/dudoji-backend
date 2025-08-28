package com.dudoji.spring.models.domain;

import com.dudoji.spring.dto.npc.NpcRequestDto;
import com.dudoji.spring.dto.npc.NpcResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Npc {
	private long npcId;
	private double lat;
	private double lng;
	private long npcSkinId;
	private String name;
	private String npcScript;
	private String description;
	private String imageUrl;
	private String questName;

	public Npc(NpcRequestDto dto) {
		this.npcId = dto.npcId();
		this.lat = dto.lat();
		this.lng = dto.lng();
		this.npcSkinId = dto.npcSkinId();
		this.name = dto.name();
		this.npcScript = dto.npcScript();
		this.description = dto.npcDescription();
		this.imageUrl = dto.imageUrl();
		this.questName = dto.questName();
	}

	public NpcResponseDto toNpcResponseDto() {
		return new NpcResponseDto(
			npcId,
			lat,
			lng,
			npcSkinId,
			name,
			npcScript,
			description,
			imageUrl,
			questName
		);
	}
}
