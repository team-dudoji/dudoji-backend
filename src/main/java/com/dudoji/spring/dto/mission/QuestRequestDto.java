package com.dudoji.spring.dto.mission;

import com.dudoji.spring.models.domain.mission.MissionUnit;
import com.dudoji.spring.models.domain.mission.QuestType;

public record QuestRequestDto(
    String title,
    String checker,
    Integer goalValue,
    MissionUnit unit,
    QuestType questType
) {
}
