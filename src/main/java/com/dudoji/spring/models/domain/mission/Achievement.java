package com.dudoji.spring.models.domain.mission;

import com.dudoji.spring.service.mission.MissionChecker;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Achievement {
    private final long id;
    private final String title;
    private final MissionChecker missionChecker;
    private final MissionUnit unit;
}
