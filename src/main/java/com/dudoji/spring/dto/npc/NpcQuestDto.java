package com.dudoji.spring.dto.npc;

import java.util.List;
import java.util.Map;

import com.dudoji.spring.dto.mission.QuestDetailDto;
import com.dudoji.spring.dto.mission.QuestDto;
import com.dudoji.spring.models.dao.quest.QuestStatus;

public record NpcQuestDto (
	long npcId,
	String name,
	String imageUrl,
	String npcScript,
	String description,
	List<QuestDetailDto> quests
) {

}

