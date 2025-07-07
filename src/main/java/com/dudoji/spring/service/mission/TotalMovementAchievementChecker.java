package com.dudoji.spring.service.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("TotalMovementAchievement")
@RequiredArgsConstructor
public class TotalMovementAchievementChecker implements MissionChecker {
    @Override
    public int check(long uid) {
        // TODO - implement logic
        return 0;
    }
}
