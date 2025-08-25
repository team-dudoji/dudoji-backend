package com.dudoji.spring.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.dto.mission.QuestRequestDto;
import com.dudoji.spring.dto.npc.NpcMetaDto;
import com.dudoji.spring.models.dao.FollowDao;
import com.dudoji.spring.models.dao.LikesDao;
import com.dudoji.spring.models.dao.NpcDao;
import com.dudoji.spring.models.dao.PinDao;
import com.dudoji.spring.models.dao.quest.MissionDao;
import com.dudoji.spring.models.dao.quest.NpcQuestDao;
import com.dudoji.spring.models.dao.skin.NpcSkinDao;
import com.dudoji.spring.models.domain.Npc;
import com.dudoji.spring.models.domain.mission.MissionUnit;
import com.dudoji.spring.models.domain.mission.QuestType;
import com.dudoji.spring.models.domain.skin.NpcSkin;

@JdbcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({NpcDao.class, NpcQuestDao.class, NpcSkinDao.class, MissionDao.class})
@TestPropertySource(properties = "SLACK_WEBHOOK_URL=http://test-slack-url.com")
public class MetaDataTest extends DBTestBase {

	@Autowired
	NpcDao npcDao;
	@Autowired
	NpcQuestDao npcQuestDao;
	@Autowired
	NpcSkinDao npcSkinDao;
	@Autowired
	MissionDao missionDao;

	Map<Long, List<Long>> npcIdToQuestId = new HashMap<>();

	@BeforeAll
	public void setUp() {
		// 스킨 만들기
		NpcSkin firstNpcSkin = new NpcSkin(1, "TestUrl1", 1);
		NpcSkin secondNpcSkin = new NpcSkin(2, "TestUrl2", 2);

		npcSkinDao.createNpcSkin(firstNpcSkin);
		npcSkinDao.createNpcSkin(secondNpcSkin);

		// Npc 만들기
		Npc firstNpc = new Npc(1, 1.0, 1.0, 1, "First Npc", "First Npc Script", "First Npc Description",
			"First Npc image Url", "First Npc quest Name");
		Npc secondNpc = new Npc(2, 1.0, 1.0, 2, "Second Npc", "Second Npc Script", "Second Npc Description",
			"Second Npc image Url", "Second Npc quest Name");
		Npc thirdNpc = new Npc(3, 1.0, 1.0, 1, "Third Npc", "Third Npc Script", "Third Npc Description",
			"Third Npc image Url", "Third Npc quest Name");

		npcDao.createNpc(firstNpc);
		npcDao.createNpc(secondNpc);
		npcDao.createNpc(thirdNpc);

		List<Npc> npcs = new ArrayList<>();
		npcs.add(firstNpc);
		npcs.add(secondNpc);
		npcs.add(thirdNpc);

		// 퀘스트 만들기
		npcs.forEach(npc -> {
			List<Long> npcQuestIds = new ArrayList<>();
			for (int i=0; i<5; i++) {
				QuestRequestDto dto = new QuestRequestDto(
					npc.getName() + i + "th Quest",
					"checker",
					10,
					MissionUnit.COUNT,
					QuestType.NPC_MAIN
				);
				long questId = missionDao.createQuest(dto);
				npcQuestIds.add(questId);
				// 디펜던시 어케하노...
				npcQuestDao.createNpcQuest(npc.getNpcId(), questId);
			}
			npcIdToQuestId.put(npc.getNpcId(), npcQuestIds);
		});
	}

	@Test
	public void QuestMetaDataTest() {
		/* 메타 데이터 생성 테스트
		 * 순서
		 * 0. 유저를 하나 만든다. 이는 schema.sql 에 명시되어 있는 101 유저로 한다.
		 * 1. Npc 를 만든다. 3번 반복한다.
		 * 2. 퀘스트를 각 Npc 당 5 개씩 만든다.
		 * 3. 두 Npc 는 각각 1개와 2개의 퀘스트를 완료한 상태로 한다.
		 * 4. 나머지 한 Npc 는 퀘스트를 아예 받지 않는다.
		 * 5. 이후 메타데이터를 받아서 원하는 값과 일치 하는 지 확인한다.
		 * 6. 원하는 값은 다음과 같다.
		 * 		a. 행 2개
		 * 		b. 1 / 5 , 2 / 5 의 퀘스트 완료 진행도
		 */

		long userId = 101;

		long firstNpcQuestId = npcIdToQuestId.get(1L).getFirst();
		List<Long> secondNpcQuestIds = npcIdToQuestId.get(2L).subList(0, 2);

		// 진행 중
		assertTrue(npcQuestDao.setQuestAsProgress(userId, firstNpcQuestId));
		secondNpcQuestIds.forEach(npcQuestId -> { assertTrue(npcQuestDao.setQuestAsProgress(userId, npcQuestId)); });

		// 진행 중을 성공으로
		assertTrue(npcQuestDao.setQuestAsCompleted(userId, firstNpcQuestId));
		secondNpcQuestIds.forEach(npcQuestId -> { assertTrue(npcQuestDao.setQuestAsCompleted(userId, npcQuestId)); });

		// 메타 데이터 받아오기
		List<NpcMetaDto> result = npcDao.getNpcMetaData(userId);
		System.out.println(result);
		assertEquals(2, result.size());

		// 맞는 지 확인
		assertThat(result)
			.containsExactlyInAnyOrder(
				new NpcMetaDto(1L, "부산광역시", "First Npc quest Name", 5, 1),
				new NpcMetaDto(2L, "서울특별시", "Second Npc quest Name", 5, 2)
			);
	}
}
