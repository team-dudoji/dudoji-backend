package com.dudoji.spring.dto.npc;

public record NpcRequestDto(
	long npcId,
	double lat,
	double lng,
	long npcSkinId,
	String name,
	String npcScript,
	String npcDescription,
	String imageUrl
) {
}
