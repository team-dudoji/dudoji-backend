package com.dudoji.spring.service.mission;

import com.dudoji.spring.dto.mission.AchievementDto;
import com.dudoji.spring.dto.mission.QuestDto;
import com.dudoji.spring.models.dao.quest.MissionDao;
import com.dudoji.spring.models.dao.PinDao;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final PinDao pinDao;
    private final MissionDao missionDao;

    public List<QuestDto> getQuestProgresses(long userId) {
        return missionDao.getQuests()
                .stream()
                .map(
                        quest ->
                            new QuestDto(
                                    quest.getTitle(),
                                    quest.getMissionChecker().check(userId),
                                    quest.getGoalValue(),
                                    quest.getType(),
                                    quest.getUnit()
                            )

                ).toList();
    }

    public List<AchievementDto> getAchievementProgresses(long userId) {
        return missionDao.getAchievements()
                .stream()
                .map(
                        achievement ->
                                new AchievementDto(
                                        achievement.getTitle(),
                                        achievement.getMissionChecker().check(userId),
                                        achievement.getUnit()
                                )

                ).toList();
    }
}
