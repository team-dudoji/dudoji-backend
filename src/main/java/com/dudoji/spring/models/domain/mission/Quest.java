package com.dudoji.spring.models.domain.mission;

import com.dudoji.spring.dto.mission.QuestRequestDto;
import com.dudoji.spring.dto.npc.NpcQuestSimpleDto;
import com.dudoji.spring.service.mission.MissionChecker;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Quest {
    private long id;
    private String title;
    private MissionChecker missionChecker;
    private int goalValue;
    private MissionUnit unit;
    private QuestType type;

    public NpcQuestSimpleDto getNpcQuestSimpleDto(long parentQuestId) {
        return new NpcQuestSimpleDto(
            id,
            title,
            missionChecker.getClass().getSimpleName(),
            goalValue,
            unit.toString(),
            type.toString(),
            parentQuestId
        );
    }

    public NpcQuestSimpleDto getNpcquestSimpleDtoWithoutParent() {
        return new NpcQuestSimpleDto(
            id,
            title,
            missionChecker.getClass().getSimpleName(),
            goalValue,
            unit.toString(),
            type.toString(),
            0
        );
    }

    public Quest(QuestRequestDto dto) {
    }
}
