package com.dudoji.spring;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.dto.mission.QuestDto;
import com.dudoji.spring.models.dao.LandmarkDao;
import com.dudoji.spring.models.dao.quest.MissionDao;
import com.dudoji.spring.models.dao.UserWalkDistanceDao;
import com.dudoji.spring.models.domain.mission.Achievement;
import com.dudoji.spring.models.domain.mission.Quest;
import com.dudoji.spring.service.mission.MissionService;

@SpringBootTest
public class MissionTest extends DBTestBase {
	@Autowired
	MissionDao missionDao;

	@Autowired
	MissionService missionService;

	@Autowired
	UserWalkDistanceDao userWalkDistanceDao;

	@Autowired
	LandmarkDao landmarkDao;

	List<Quest> quests;
	List<Achievement> achievements;

	private final int testUserId = 101;

	@BeforeEach
	public void init() {
		quests = missionDao.getQuests();
		achievements = missionDao.getAchievements();
	}

	@Test
	public void totalTest() {
		userWalkDistanceDao.createUserWalkDistance(testUserId, LocalDate.now(), 1000);
		landmarkDao.setDetect(testUserId, 1);

		List<QuestDto> questDtos =  missionService.getQuestProgresses(testUserId);

		questDtos.forEach(System.out::println);
	}
}
