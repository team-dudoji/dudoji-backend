package com.dudoji.spring.service.mission;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dudoji.spring.models.dao.UserWalkDistanceDao;

@Service("TotalMovementAchievement")
@RequiredArgsConstructor
public class TotalMovementAchievementChecker implements MissionChecker {
    private final UserWalkDistanceDao userWalkDistanceDao;
    @Override
    public int check(long uid) {
        return userWalkDistanceDao.getTotalDistance(uid);
    }
}
