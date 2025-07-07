package com.dudoji.spring.service.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("LandmarkAchievement")
@RequiredArgsConstructor
public class LandmarkAchievementChecker implements MissionChecker {
    @Override
    public int check(long uid) {
        return 0;
    }
}
