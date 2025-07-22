package com.dudoji.spring.service.mission;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.dudoji.spring.models.dao.LandmarkDao;

@Service("NewLandmarkQuest")
@RequiredArgsConstructor
public class NewLandmarkQuestChecker implements MissionChecker {
    private final LandmarkDao landmarkDao;
    @Override
    public int check(long uid) {
        return landmarkDao.getNumOfLandmarksByUserIdAndDates(
            uid,
            LocalDate.now(),
            LocalDate.now().plusDays(1)
        );
    }
}
