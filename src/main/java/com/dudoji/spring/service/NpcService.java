package com.dudoji.spring.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.cors.PreFlightRequestHandler;

import com.dudoji.spring.dto.mission.QuestDetailDto;
import com.dudoji.spring.dto.mission.QuestRequestDto;
import com.dudoji.spring.dto.npc.NpcDto;
import com.dudoji.spring.dto.npc.NpcQuestDto;
import com.dudoji.spring.dto.npc.NpcQuestSimpleDto;
import com.dudoji.spring.dto.npc.NpcQuestStatusDto;
import com.dudoji.spring.dto.npc.NpcRequestDto;
import com.dudoji.spring.dto.npc.NpcResponseDto;
import com.dudoji.spring.dto.npc.NpcSkinDto;
import com.dudoji.spring.models.dao.NpcDao;
import com.dudoji.spring.models.dao.quest.MissionDao;
import com.dudoji.spring.models.dao.quest.NpcQuestDao;
import com.dudoji.spring.models.dao.quest.QuestStatus;
import com.dudoji.spring.models.dao.skin.NpcSkinDao;
import com.dudoji.spring.models.domain.Npc;
import com.dudoji.spring.models.domain.mission.Quest;
import com.dudoji.spring.models.domain.skin.NpcSkin;
import com.dudoji.spring.util.BitmapUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class NpcService {

	@Autowired
	NpcDao npcDao;

	@Autowired
	NpcSkinDao npcSkinDao;

	@Autowired
	NpcQuestDao npcQuestDao;

	@Autowired
	MissionDao missionDao;
	@Autowired
	private PreFlightRequestHandler preFlightRequestHandler;

	public Npc getNpcById(long npcId) {
		return npcDao.getNpcById(npcId);
	}

	public List<NpcQuestSimpleDto> getAllQuests() {
		return missionDao.getAllQuests().stream().map(Quest::getNpcquestSimpleDtoWithoutParent).toList();
	}

	public List<NpcSkin> getAllNpcSkins() {
		return npcSkinDao.getAllNpcSkins();
	}

	public List<NpcDto> getNpcsByRadius(double lat, double lng, double radius, long userId) {
		// Calculate lat, lng value based on radius
		double deltaLat = Math.toDegrees(radius / BitmapUtil.EARTH_RADIUS);
		double deltaLng = Math.toDegrees(radius / BitmapUtil.EARTH_RADIUS * Math.cos(Math.toRadians(lat)));

		double minLat = lat - deltaLat;
		double maxLat = lat + deltaLat;
		double minLng = lng - deltaLng;
		double maxLng = lng + deltaLng;

		List<Npc> npcs = npcDao.getNpcsByRadius(minLat, minLng, maxLat, maxLng);

		if (npcs.isEmpty()) { return List.of(); }

		List<Long> skinIds = npcs.stream().map(Npc::getNpcSkinId).distinct().toList();

		List<NpcSkin> skins = npcSkinDao.getNpcSkinByIds(skinIds);
		Map<Long, String> skinUrlMap = skins.stream()
			.collect(Collectors.toMap(
				NpcSkin::getNpcSkinId,
				NpcSkin::getImageUrl
			));

		return npcs.stream()
			.map(npc -> {
				NpcDto dto = new NpcDto(npc);
				dto.setNpcSkinUrl(skinUrlMap.get(npc.getNpcSkinId()));
				dto.setHasQuest(!npcQuestDao.getNotAssignedNpcQuests(userId, npc.getNpcId()).isEmpty());
				return dto;
			})
			.toList();
	}

	public NpcQuestDto getAllNpcQuests(long npcId, long userId) {
		Npc npc = npcDao.getNpcById(npcId);

		List<Long> questIds = npcQuestDao.getNpcQuestByNpcId(npcId);

		// Get All Quests of npc
		List<Quest> quests = missionDao.getQuestsByQuestIds(questIds); // 메인 인 지 서브인 지 기록되어 있음.

		List<NpcQuestStatusDto> questStatus = npcQuestDao.getAllNpcQuestByUserIdAndQuestIds(userId, questIds);
		// List -> Map 접근 시간을 용이하게 합니다.
		Map<Long, NpcQuestStatusDto> statusMap = questStatus.stream()
			.collect(Collectors.toMap(NpcQuestStatusDto::questId, Function.identity()));

		Map<Long, Long> questDependency = npcQuestDao.getQuestDependencies(questIds);
		for (long key : questDependency.keySet()) {
			log.info("key: {}, value: {}", key, questDependency.get(key));
		}

		List<QuestDetailDto> questDetailDto = quests.stream()
			.filter(quest -> { // 보여주지 않을 퀘스트 거르기
				long questId = quest.getId();
				long parentQuestId = questDependency.getOrDefault(questId, 0L);
				NpcQuestStatusDto parentStatus = statusMap.get(parentQuestId);

				return (parentQuestId == 0L) || // TODO: 여기서 Sub 와 EMERGENCY 인 경우 솎아내면 됩니다.
					(parentStatus != null && QuestStatus.COMPLETED.equals(parentStatus.status()));
			})
			.map(quest -> {
				long questId = quest.getId();
				// Null 처리
				NpcQuestStatusDto status = Optional.ofNullable(statusMap.get(questId))
					.orElseGet(() -> new NpcQuestStatusDto(
						quest.getId(),
						userId,
						QuestStatus.NOT_ASSIGNED,
						null,
						null
					));

				return new QuestDetailDto(
					quest,
					userId,
					Objects.requireNonNullElseGet(status, () -> new NpcQuestStatusDto(
						quest.getId(),
						userId,
						QuestStatus.NOT_ASSIGNED,
						null,
						null))
				);
			}).toList();

		return new NpcQuestDto(
			npc.getNpcId(),
			npc.getName(),
			npc.getImageUrl(),
			npc.getNpcScript(),
			npc.getDescription(),
			questDetailDto
		);
	}

	/**
	 * 단일 npc 에 대한 모든 퀘스트를 불러온다.
	 * @param npcId npc 의 아이디
	 * @return Quest List
	 */
	public List<NpcQuestSimpleDto> getAllNpcQuests(long npcId) {
		List<Long> questIds = npcQuestDao.getNpcQuestByNpcId(npcId);
		if (questIds.isEmpty()) { return List.of(); }
		Map<Long, Long> questDependency = npcQuestDao.getQuestDependencies(questIds);
		// Get All Quests of npc
		return missionDao.getQuestsByQuestIds(questIds)
			.stream()
			.map(quest -> {
				long questId = quest.getId();
				long parentQuestId = questDependency.getOrDefault(questId, 0L);

				return quest.getNpcQuestSimpleDto(parentQuestId);
			})
			.toList(); // 메인 인 지 서브인 지 기록되어 있음.
	}

	public List<NpcResponseDto> getAllNpcs() {
		return npcDao.getAllNpcs().stream().map(Npc::toNpcResponseDto).toList();
	}

	/*
	 About Npc CRUD
	 */

	/**
	 * dto 를 기반으로 npc 를 생성합니다.
	 * @param dto 만들고 싶은 npc 의 정보를 담은 객체
	 * @return 생성 성공 여부
	 */
	public boolean createNpc(NpcRequestDto dto) {
		return npcDao.createNpc(new Npc(dto)) > 0;
	}

	/**
	 * npc id 를 통해 npc 를 삭제합니다.
	 * @param npcId npc 의 id
	 * @return 삭제 성공 여부
	 */
	public boolean deleteNpc(long npcId) {
		return npcDao.deleteNpc(npcId);
	}

	/**
	 * npc 를 업데이트 합니다.
	 * @param dto 업데이트 내용을 답고 있는 dto
	 * @return 업데이트 성공 여부
	 */
	public boolean updateNpc(NpcRequestDto dto) {
		log.info("[updateNpc] {}", dto);
		return npcDao.updateNpc(new Npc(dto));
	}

	/*
	 About QUEST DEPENDENCY
	 */

	/**
	 * 퀘스트의 종속성을 추가합니다.
	 * @param parentQuestId 부모 퀘스트 아이디
	 * @param childQuestId 아들 퀘스트 아이디
	 * @return 성공 여부
	 */
	public boolean createQuestDependency(long parentQuestId, long childQuestId) {
		return npcQuestDao.createQuestDependency(parentQuestId, childQuestId);
	}

	/**
	 * 퀘스트 종속성을 삭제합니다.
	 * @param parentQuestId 부모 퀘스트 아이디
	 * @param childQuestId 아들 퀘스트 아이디
	 * @return 성공 여부
	 */
	public boolean deleteQuestDependency(long parentQuestId, long childQuestId) {
		return npcQuestDao.deleteQuestDependency(parentQuestId, childQuestId);
	}

	/*
	 About Npc Quest
	 */

	/**
	 * Npc 에게 퀘스트를 종속시킵니다.
	 * @param npcId npc 아이디
	 * @param questId 퀘스트 아이디
	 * @return 성공 여부
	 */
	public boolean createNpcQuest(long npcId, long questId) {
		return npcQuestDao.createNpcQuest(npcId, questId);
	}

	/**
	 * Npc 에게 있는 퀘스트를 삭제합니다.
	 * @param npcId npc 아이디
	 * @param questId 퀘스트 아이디
	 * @return 성공 여부
	 */
	public boolean deleteNpcQuest(long npcId, long questId) {
		return npcQuestDao.deleteNpcQuest(npcId, questId);
	}

	/*
	 About Quest CRUD
	 */

	public boolean createQuest(QuestRequestDto dto) {
		return missionDao.createQuest(dto) > 0;
	}

	public boolean deleteQuest(long questId) {
		return missionDao.deleteQuest(questId);
	}

	public boolean updateQuest(QuestRequestDto dto) {
		// return missionDao.updateQuest(new Quest(dto));
		return false;
	}

	/*
	 About Npc Skin
	 */

	/**
	 * npc skin 을 만듭니다.
	 * @param dto 스킨에 대한 정보를 담고 있는 dto
	 * @return 만들어진 스킨의 아이디
	 */
	public long createNpcSkin(NpcSkinDto dto) {
		return npcSkinDao.createNpcSkin(new NpcSkin(dto));
	}

	/**
	 * npc skin 을 스킨 아이디를 통해 삭제합니다.
	 * @param skinId 삭제하고픈 npc skin id
	 * @return 성공 여부
	 */
	public boolean deleteNpcSkin(long skinId) {
		return npcSkinDao.deleteNpcSkin(skinId);
	}
}
