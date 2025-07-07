package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.domain.mission.Achievement;
import com.dudoji.spring.models.domain.mission.MissionUnit;
import com.dudoji.spring.models.domain.mission.Quest;
import com.dudoji.spring.models.domain.mission.QuestType;
import com.dudoji.spring.service.mission.MissionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MissionDao {

    private static final String GET_QUESTS = """
            SELECT questId, title, checker, goalValue, unit, questType
            FROM Quest
            """;

    private static final String GET_ACHIEVEMENTS = """
            SELECT achievementId, title, checker, unit
            FROM Achievement
            """;

    private final JdbcClient jdbcClient;
    private final ApplicationContext applicationContext;

    public List<Quest> getQuests() {
        return jdbcClient.sql(GET_QUESTS)
                .query((rs, num) ->
                        new Quest(
                                rs.getLong("questId"),
                                rs.getString("title"),
                                (MissionChecker) applicationContext.getBean(rs.getString("checker")),
                                rs.getInt("goalValue"),
                                MissionUnit.valueOf(rs.getString("unit")),
                                QuestType.valueOf(rs.getString("questType")))
                ).list();
    }

    public List<Achievement> getAchievements() {
        return jdbcClient.sql(GET_ACHIEVEMENTS)
                .query((rs, num) ->
                        new Achievement(
                                rs.getLong("achievementId"),
                                rs.getString("title"),
                                (MissionChecker) applicationContext.getBean(rs.getString("checker")),
                                MissionUnit.valueOf(rs.getString("unit"))
                )).list();
    }
}
