package com.dudoji.spring.service.mission;

import com.dudoji.spring.models.dao.PinDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service("DailyPinQuest")
@RequiredArgsConstructor
public class DailyPinQuestChecker implements MissionChecker {
    private final PinDao pinDao;

    @Override
    public int check(long uid) {
        return pinDao.getNumOfPinByUserIdAndDates(
                uid,
                Date.valueOf(LocalDate.now()),
                Date.valueOf(LocalDate.now().plusDays(1))
        );
    }
}
