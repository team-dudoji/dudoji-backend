package com.dudoji.spring.dto.mission;

import java.time.LocalDate;

import com.dudoji.spring.dto.npc.NpcQuestStatusDto;
import com.dudoji.spring.models.dao.quest.QuestStatus;
import com.dudoji.spring.models.domain.mission.MissionUnit;
import com.dudoji.spring.models.domain.mission.Quest;
import com.dudoji.spring.models.domain.mission.QuestType;

import lombok.Getter;

@Getter
public class QuestDetailDto {
	private final Long id;
	private final String title;
	private final int currentValue;
	private final int goalValue;
	private final QuestType type;
	private final MissionUnit unit;
	private final QuestStatus status;
	private final LocalDate startedAt;
	private final LocalDate completedAt;

	public QuestDetailDto(Quest quest, long userId, NpcQuestStatusDto npcQuestStatusDto) {
		this.id = quest.getId();
		this.title = quest.getTitle();
		this.currentValue = quest.getMissionChecker().check(userId);
		this.goalValue = quest.getGoalValue();
		this.type = quest.getType();
		this.unit = quest.getUnit();
		this.status = npcQuestStatusDto.status();
		this.startedAt = npcQuestStatusDto.startedAt();
		this.completedAt = npcQuestStatusDto.completedAt();
	}
}
