package com.dudoji.spring.models.dao.quest;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.dudoji.spring.dto.npc.NpcQuestStatusDto;

@Repository("NpcQuestDao")
public class NpcQuestDao {

	@Autowired
	private JdbcClient jdbcClient;

	private static final String INSERT_NPC_QUEST = """
		INSERT INTO NpcQuest (npcId, questId) VALUES
		(:npcId, :questId)
		""";

	// private static final String GET_NPC_QUEST_BY_QUEST_ID = """
	// 	SELECT * FROM NpcQuest WHERE questId = :questId
	// 	""";

	private static final String GET_NPC_QUEST_BY_NPC_ID = """
		SELECT questId FROM NpcQuest WHERE npcId = :npcId
		""";

	private static final String DELETE_NPC_QUEST = """
		DELETE FROM NpcQuest WHERE questId = :questId AND npcId = :npcId
		""";

	private static final String GET_STATUS_QUEST = """
		SELECT * FROM UserNpcQuestStatus WHERE userId = :userId AND status = :status
		""";

	private static final String GET_NOT_ASSIGNED_QUEST = """
		WITH userDone AS (
			SELECT questId
			FROM UserNpcQuestStatus
			WHERE userId = :userId
			AND status IN ('PROGRESS', 'COMPLETED')
		)
		
		SELECT q.questId
		FROM Quest q
		JOIN NpcQuest nq
		ON nq.questId = q.questId
		WHERE nq.npcId = :npcId
		AND q.questId NOT IN (SELECT questId FROM userDone)
		""";

	private static final String CREATE_QUEST_DEPENDENCY = """
		INSERT INTO QuestDependency (parentQuestId, childQuestId) VALUES (:parentQuestId, :childQuestId)
		""";

	private static final String DELETE_QUEST_DEPENDENCY = """
		DELETE FROM QuestDependency WHERE parentQuestId = :parentQuestId AND childQuestId = :childQuestId
		""";

	private static final String GET_ALL_NPC_QUEST_BY_USERID_QUEST_IDS = """
		SELECT * FROM UserNpcQuestStatus WHERE userId = :userId AND questId IN (:questIds)
		""";

	private static final String GET_QUEST_DEPENDENCY = """
		SELECT parentQuestId, childQuestId FROM QuestDependency
		WHERE childQuestId IN (:questIds)
		""";

	private static final String UPDATE_QUEST_AS_PROGRESS = """
		INSERT INTO UserNpcQuestStatus (userId, questId, status) VALUES (:userId, :questId, :status::quest_progress)	
		""";

	private static final String UPDATE_QUEST_AS_COMPLETED = """
		UPDATE UserNpcQuestStatus SET status = 'COMPLETED'::quest_progress WHERE questId = :questId AND userId = :userId
		""";

	private final RowMapper<NpcQuestStatusDto> npcQuestStatusDtoRowMapper = (rs, rowNum) -> {
		String rawStatus = rs.getString("status");
		QuestStatus status = (rawStatus == null || rawStatus.isBlank()) ? QuestStatus.NOT_ASSIGNED : QuestStatus.valueOf(rawStatus);

		return new NpcQuestStatusDto(
			rs.getLong("userId"),
			rs.getLong("questId"),
			status,
			rs.getDate("startedAt").toLocalDate(),
			rs.getDate("completedAt").toLocalDate()
		);
	};

	/**
	 * npc 와 퀘스트를 이어줍니다.
	 * @param npcId npc id
	 * @param questId quest id
	 * @return 삽입 성공 여부
	 */
	public boolean createNpcQuest(long npcId, long questId) {
		return jdbcClient.sql(INSERT_NPC_QUEST)
			.param("npcId", npcId)
			.param("questId", questId)
			.update() > 0;
	}

	/**
	 * npc 가 가지고 있는 퀘스트 목록을 아이디 형태로 제시합니다.
	 * @param npcId 조회할 npc id
	 * @return quest id list
	 */
	public List<Long> getNpcQuestByNpcId(long npcId) {
		return jdbcClient.sql(GET_NPC_QUEST_BY_NPC_ID)
			.param("npcId", npcId)
			.query(Long.class)
			.list();
	}

	/**
	 * 해당하는 레코드를 삭제한다.
	 * @param npcId npc id
	 * @param questId quest id
	 * @return 삭제 성공 여부
	 */
	public boolean deleteNpcQuest(long npcId, long questId) {
		return jdbcClient.sql(DELETE_NPC_QUEST)
			.param("npcId", npcId)
			.param("questId", questId)
			.update() > 0;
	}

	/**
	 * Status 에 따른 Quest 를 받아옵니다.
	 * @param userId user id
	 * @param status QuestStatus PROGRESS , COMPLETED 둘 중 하나
	 * @return NpcQuestStatusDto list
	 */
	public List<NpcQuestStatusDto> getNpcQuestByStatus(long userId, QuestStatus status) {
		return jdbcClient.sql(GET_STATUS_QUEST)
			.param("userId", userId)
			.param("status", status.name())
			.query(npcQuestStatusDtoRowMapper)
			.list();
	}

	/**
	 * Npc 의 퀘스트 중 아직 받지 않은 퀘스트를 받아줍니다.
	 * @param userId user Id
	 * @param npcId npc Id
	 * @return Not assigned quest id list
	 */
	public List<Long> getNotAssignedNpcQuests(long userId, long npcId) {
		return jdbcClient.sql(GET_NOT_ASSIGNED_QUEST)
			.param("userId", userId)
			.param("npcId", npcId)
			.query((rs, rowNum) -> rs.getLong("questId"))
			.list();
	}

	/**
	 * 퀘스트 간의 선행 관계를 만듭니다.
	 * @param parentQuestId 부모 퀘스트 아이디
	 * @param childQuestId 자식 퀘스트 아이디
	 * @return 성공 여부
	 */
	public boolean createQuestDependency(long parentQuestId, long childQuestId) {
		return jdbcClient.sql(CREATE_QUEST_DEPENDENCY)
			.param("parentQuestId", parentQuestId)
			.param("childQuestId", childQuestId)
			.update() > 0;
	}

	/**
	 * 퀘스트 의존성을 제거합니다.
	 * @param parentQuestId 부모 퀘스트 아이디
	 * @param childQuestId 자식 퀘스트 아이디
	 * @return 삭제 성공 여부
	 */
	public boolean deleteQuestDependency(long parentQuestId, long childQuestId) {
		return jdbcClient.sql(DELETE_QUEST_DEPENDENCY)
			.param("parentQuestId", parentQuestId)
			.param("childQuestId", childQuestId)
			.update() > 0;
	}

	/**
	 * quest id 리스트를 통해 유저의 해당 퀘스트 진행 여부를 반환합니다.
	 * @param userId user id
	 * @param questIds quest id list
	 * @return Npc Quest Status Dto list
	 */
	public List<NpcQuestStatusDto> getAllNpcQuestByUserIdAndQuestIds(long userId, List<Long> questIds) {
		return jdbcClient.sql(GET_ALL_NPC_QUEST_BY_USERID_QUEST_IDS)
			.param("userId", userId)
			.param("questIds", questIds)
			.query(npcQuestStatusDtoRowMapper)
			.list();
	}

	/**
	 * questId 를 통해 해당 퀘스트 id의 부모 퀘스트 id를 받아옵니다.
	 * @param questIds 궁금한 퀘스트 id 목록
	 * @return 자식 id를 키로 가지고, 부모 아이디를 값으로 가지는 map 반환
	 */
	public Map<Long, Long> getQuestDependencies(List<Long> questIds) {
		List<AbstractMap.SimpleEntry<Long, Long>> dependenciesList = jdbcClient.sql(GET_QUEST_DEPENDENCY)
			.param("questIds", questIds)
			.query((rs, rowNum) -> new AbstractMap.SimpleEntry<>(
				rs.getLong("childQuestId"),
				rs.getLong("parentQuestId")
			)).list();

		return dependenciesList.stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue
			));
	}

	/**
	 * 해당 퀘스트를 진행 상태로 UserNpcQuestStatus 에 업데이트 합니다. 퀘스트 수락의 역할을 합니다.
	 * @param userId user Id
	 * @param questId quest Id
	 * @return 쿼리문 성공 여부
	 */
	public boolean setQuestAsProgress(long userId, long questId) {
		return jdbcClient.sql(UPDATE_QUEST_AS_PROGRESS)
			.param("userId", userId)
			.param("questId", questId)
			.param("status", "PROGRESS")
			.update() > 0;
	}

	/**
	 * 진행 중인 퀘스트를 완료로 표시합니다.
	 * @param userId user Id
	 * @param questId quest Id
	 * @return SQLException if there is no quest in progress, or true.
	 */
	public boolean setQuestAsCompleted(long userId, long questId) {
		return jdbcClient.sql(UPDATE_QUEST_AS_COMPLETED)
			.param("userId", userId)
			.param("questId", questId)
			.update() > 0;
	}
}