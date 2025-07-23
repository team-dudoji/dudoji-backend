package com.dudoji.spring.service.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.dudoji.spring.models.dao.LandmarkDao;

@Service("LandmarkCountQuest")
@RequiredArgsConstructor
public class LandmarkCountQuestChecker implements MissionChecker {
    private final LandmarkDao landmarkDao;
    @Override
    public int check(long uid) {
        return landmarkDao.getNumOfDetectedLandmarksByUserId(uid);
    }
}
