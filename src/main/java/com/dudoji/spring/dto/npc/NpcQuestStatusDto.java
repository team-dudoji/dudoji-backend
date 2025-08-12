package com.dudoji.spring.dto.npc;

import java.time.LocalDate;

import com.dudoji.spring.models.dao.quest.QuestStatus;

public record NpcQuestStatusDto (
	long userId,
	long questId,
	QuestStatus status,
	LocalDate startedAt,
	LocalDate completedAt
) {

}

