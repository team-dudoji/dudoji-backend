package com.dudoji.spring.service.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("LandmarkCountQuest")
@RequiredArgsConstructor
public class LandmarkCountQuestChecker implements MissionChecker {
    @Override
    public int check(long uid) {
        // TODO - implement logic
        return 0;
    }
}
