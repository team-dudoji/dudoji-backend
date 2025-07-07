package com.dudoji.spring.service.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("FogAchievement")
@RequiredArgsConstructor
public class FogAchievementChecker implements MissionChecker {
    @Override
    public int check(long uid) {
        return 0;
    }
}
