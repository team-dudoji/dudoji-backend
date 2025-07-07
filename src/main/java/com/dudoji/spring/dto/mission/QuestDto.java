package com.dudoji.spring.dto.mission;

import com.dudoji.spring.models.domain.mission.MissionUnit;
import com.dudoji.spring.models.domain.mission.QuestType;

public record QuestDto(
        String title,
        int currentValue,
        int goalValue,
        QuestType type,
        MissionUnit unit
) {

}