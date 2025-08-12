package com.dudoji.spring.dto.npc;

import com.dudoji.spring.models.domain.Npc;

import lombok.Data;

@Data
public class NpcDto {
	long npcId;
	double lat;
	double lng;
	String name;
	String npcSkinUrl;
	boolean hasQuest;

	public Npc toDomain() {
		return new Npc(
			npcId,
			lat,
			lng,
			0,  // 기본값 0
			name,
			null,
			null,
			null // 얘네는 값이 없음
		);
	}

	public NpcDto(Npc npc) {
		npcId = npc.getNpcId();
		lat = npc.getLat();
		lng = npc.getLng();
		name = npc.getName();
	}
}
