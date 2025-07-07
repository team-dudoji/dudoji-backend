package com.dudoji.spring.dto.mission;

import com.dudoji.spring.models.domain.mission.MissionUnit;

public record AchievementDto(
        String title,
        int value,
        MissionUnit unit
) {
}
