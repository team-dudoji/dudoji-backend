package com.dudoji.spring.service.mission;

import java.sql.Date;
import java.time.LocalDate;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dudoji.spring.dto.UserWalkDistancesDto;
import com.dudoji.spring.models.dao.UserWalkDistanceDao;

@Service("DailyMovementQuest")
@RequiredArgsConstructor
public class DailyMovementQuestChecker implements MissionChecker {

    private final UserWalkDistanceDao userWalkDistanceDao;

    @Override
    public int check(long uid) {
        return userWalkDistanceDao.getUserWalkDistanceByIdOnDuration(
            uid,
            LocalDate.now(),
            LocalDate.now()
        ).userWalkDistances
            .stream()
            .findFirst() // 같은 날 조회했으니, 첫번째가 원하는 값
            .map(UserWalkDistancesDto.UserWalkDistanceDto::getDistance)
            .orElse(0); // 오늘의 기록이 없을 경우 0
    }
}
