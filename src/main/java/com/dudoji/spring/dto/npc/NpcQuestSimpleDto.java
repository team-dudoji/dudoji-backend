package com.dudoji.spring.dto.npc;

public record NpcQuestSimpleDto(
	long questId,
	String title,
	String checker,
	int goalValue,
	String unit,
	String questType,
	long parentQuestId
) {
}
