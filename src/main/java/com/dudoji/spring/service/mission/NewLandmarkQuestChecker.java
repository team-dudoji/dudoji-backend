package com.dudoji.spring.service.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("NewLandmarkQuest")
@RequiredArgsConstructor
public class NewLandmarkQuestChecker implements MissionChecker {
    @Override
    public int check(long uid) {
        return 0;
    }
}
