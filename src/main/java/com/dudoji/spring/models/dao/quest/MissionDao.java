package com.dudoji.spring.models.dao.quest;

import com.dudoji.spring.dto.mission.QuestDetailDto;
import com.dudoji.spring.dto.mission.QuestRequestDto;
import com.dudoji.spring.models.domain.mission.Achievement;
import com.dudoji.spring.models.domain.mission.MissionUnit;
import com.dudoji.spring.models.domain.mission.Quest;
import com.dudoji.spring.models.domain.mission.QuestType;
import com.dudoji.spring.service.mission.MissionChecker;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.RowMapper;
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
    private static final String GET_QUESTS_BY_IDS = """
        SELECT questId, title, checker, goalValue, unit, questType
        FROM Quest
        WHERE questId in (:questIds)
        """;

    private static final String GET_ACHIEVEMENTS = """
            SELECT achievementId, title, checker, unit
            FROM Achievement
            """;

    private static final String CREATE_QUEST = """
        INSERT INTO Quest(title, checker, goalValue, unit, questType)
        VALUES (:title, :checker, :goalValue, :unit::mission_unit, :questType::quest_type)
        RETURNING questId;
        """;

    private static final String UPDATE_QUEST = """
        UPDATE Quest
        SET title = :title,
            checker = :checker,
            goalValue = :goalValue,
            unit = :unit::mission_unit,
            questType = :questType::quest_type
        WHERE questId = :questId
        RETURNING questId;
        """;

    private static final String DELETE_QUEST = """
        DELETE FROM Quest
        WHERE questId = :questId
        """;

    private static final String GET_ALL_QUESTS = """
        SELECT * FROM Quest
        """;

    private final JdbcClient jdbcClient;
    private final ApplicationContext applicationContext;

    private RowMapper<Quest> questRowMapper;

    @PostConstruct
    void init() {
        questRowMapper
            = (rs, num) ->
            new Quest(
                rs.getLong("questId"),
                rs.getString("title"),
                (MissionChecker) applicationContext.getBean(rs.getString("checker")),
                rs.getInt("goalValue"),
                MissionUnit.valueOf(rs.getString("unit")),
                QuestType.valueOf(rs.getString("questType")));
    }

    public List<Quest> getQuests() {
        return jdbcClient.sql(GET_QUESTS)
                .query(questRowMapper).list();
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

    public List<Quest> getQuestsByQuestIds(List<Long> questIds) {
        return jdbcClient.sql(GET_QUESTS_BY_IDS)
            .param("questIds", questIds)
            .query(questRowMapper).list();
    }

    /**
     * 퀘스트를 만듭니다.
     * @param quest 만들고 싶은 퀘스트의 정보를 담은 변수
     * @return 생성된 퀘스트의 id
     */
    public long createQuest(QuestRequestDto quest) {
        return jdbcClient.sql(CREATE_QUEST)
            .param("title", quest.title())
            .param("checker", quest.checker())
            .param("goalValue", quest.goalValue())
            .param("unit", quest.unit().name())
            .param("questType", quest.questType().name())
            .query(Long.class)
            .single();
    }

    /**
     * 퀘스트를 업데이트 합니다.
     * @param quest 업데이트 하고 싶은 퀘스트의 정보
     * @return 업데이트 된 퀘스트의 id
     */
    public long updateQuest(Quest quest) {
        return jdbcClient.sql(UPDATE_QUEST)
            .param("title", quest.getTitle())
            .param("checker", quest.getMissionChecker().toString())
            .param("goalValue", quest.getGoalValue())
            .param("unit", quest.getUnit().toString())
            .param("questType", quest.getType().toString())
            .query(Long.class)
            .single();
    }

    /**
     * 퀘스트를 삭제합니다.
     * @param questId 삭제하고 싶은 퀘스트의 아이디
     * @return 삭제 성공 여부
     */
    public boolean deleteQuest(long questId) {
        return jdbcClient.sql(DELETE_QUEST)
            .param("questId", questId)
            .update() > 0;
    }

    /**
     * 모든 퀘스트를 받아옵니다
     * @return list of quest
     */
    public List<Quest> getAllQuests() {
        return jdbcClient.sql(GET_ALL_QUESTS).query(questRowMapper).list();
    }
}
