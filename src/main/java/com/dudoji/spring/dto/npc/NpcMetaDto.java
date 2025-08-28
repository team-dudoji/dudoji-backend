package com.dudoji.spring.dto.npc;

public record NpcMetaDto(
	long npcSkinId,
	String locationName,
	String questName,
	int numOfQuests,
	int numOfClearedQuests
) {
}
