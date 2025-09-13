package com.dudoji.spring.models.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import com.dudoji.spring.dto.npc.NpcDto;
import com.dudoji.spring.dto.npc.NpcMetaDto;
import com.dudoji.spring.models.domain.Npc;

@Repository("NpcDao")
public class NpcDao {

	@Autowired
	private JdbcClient jdbcClient;

	private static final String INSERT_NPC = """
		INSERT INTO Npc (lat, lng, npcSkinId, name, npcScript, description, imageUrl, questName) VALUES
		(:lat, :lng, :npcSkinId, :name, :npcScript, :description, :imageUrl, :questName)
		RETURNING npcId
		""";

	private static final String GET_ALL_NPC = """
		SELECT * FROM Npc
		""";

	private static final String GET_NPC_BY_ID = """
		SELECT * FROM Npc WHERE npcId = :npcId
		""";
	private static final String GET_NPC_BY_RADIUS = """
       SELECT * FROM Npc
       WHERE lat BETWEEN :minLat AND :maxLat
       AND lng BETWEEN :minLng AND :maxLng;
       """;

	private static final String UPDATE_NPC = """
		UPDATE Npc
		    SET
				lat = :lat,
				lng = :lng,
				npcSkinId = :npcSkinId,
				name = :name,
				npcScript = :npcScript,
				description = :description,
				imageUrl = :imageUrl
		    	questName = :questName
		WHERE npcId = :npcId;
		""";

	private static final String DELETE_NPC = """
		DELETE FROM Npc WHERE npcId = :npcId;
		""";

	private static final String GET_NPC_META_DATA = """
		WITH completed AS (
			SELECT qs.questId
			FROM UserNpcQuestStatus qs
			WHERE qs.userId = :userId
				AND qs.status = 'COMPLETED'::quest_progress
		),
		
		npcClearedCounts AS (
		  SELECT nq.npcId, COUNT(*) AS numOfClearedQuests
		  FROM NpcQuest nq
		  JOIN completed c USING (questId)
		  GROUP BY nq.npcId
		),
		
		npcQuestCounts AS (
			SELECT nq.npcId, COUNT(*) AS numOfQuests
			FROM NpcQuest nq
			GROUP BY nq.npcId
		)
		
		SELECT
			ns.npcSkinId,
			ns.regionId,
			r.name       AS regionName,
			n.questName,
			nt.numOfQuests AS numOfQuests,
			nc.numOfClearedQuests AS numOfClearedQuests,
          lat,
          lng
		FROM Npc n
		JOIN NpcSkin ns ON ns.npcSkinId = n.npcSkinId
		JOIN Region r ON r.regionId = ns.regionId
		JOIN npcQuestCounts nt ON nt.npcId = n.npcId
		LEFT JOIN npcClearedCounts nc ON nc.npcId = n.npcId
		ORDER BY ns.regionId, ns.npcSkinId
		""";

	private final RowMapper<Npc> npcRowMapper = (rs, rowNum) -> {
		return new Npc(
			rs.getLong("npcId"),
			rs.getDouble("lat"),
			rs.getDouble("lng"),
			rs.getLong("npcSkinId"),
			rs.getString("name"),
			rs.getString("npcScript"),
			rs.getString("description"),
			rs.getString("imageUrl"),
			rs.getString("questName")
		);
	};

	private final RowMapper<NpcMetaDto> npcMetaDtoRowMapper = (rs, rowNum) -> {
		return new NpcMetaDto(
			rs.getLong("npcSkinId"),
			rs.getString("regionName"),     // locationName <- regionName 매핑
			rs.getString("questName"),
			rs.getInt("numOfQuests"),
			rs.getInt("numOfClearedQuests"),
            rs.getDouble("lat"),
            rs.getDouble("lng")
		);
	};

	/**
	 * npc 를 만듭니다.
	 * @param npc npc 정보를 담은 오브젝트
	 * @return npc id를 반환합니다.
	 */
	public long createNpc(Npc npc) {
		return jdbcClient.sql(INSERT_NPC)
			.param("lat", npc.getLat())
			.param("lng", npc.getLng())
			.param("npcSkinId", npc.getNpcSkinId())
			.param("name", npc.getName())
			.param("npcScript", npc.getNpcScript())
			.param("description", npc.getDescription())
			.param("imageUrl", npc.getImageUrl())
			.param("questName", npc.getQuestName())
			.query(Long.class)
			.single();
	}

	/**
	 * 모든 Npc 들을 반환합니다.
	 * @return npc 리스트
	 */
	public List<Npc> getAllNpcs() {
		return jdbcClient.sql(GET_ALL_NPC)
			.query(npcRowMapper)
			.list();
	}

	/**
	 * npc Id 로 npc 를 반환합니다.
	 * @param npcId 원하는 npc id
	 * @return npc 객체
	 */
	public Npc getNpcById(long npcId) {
		return jdbcClient.sql(GET_NPC_BY_ID)
			.param("npcId", npcId)
			.query(npcRowMapper)
			.single();
	}

	/**
	 * 범위로 Npc 들을 반환합니다.
	 * @param minLat 최소 lat
	 * @param minLng 최소 lng
	 * @param maxLat 최대 lat
	 * @param maxLng 최대 lng
	 * @return
	 */
	public List<Npc> getNpcsByRadius(double minLat, double minLng, double maxLat, double maxLng) {
		return jdbcClient.sql(GET_NPC_BY_RADIUS)
			.param("minLat", minLat)
			.param("maxLat", maxLat)
			.param("minLng", minLng)
			.param("maxLng", maxLng)
			.query(npcRowMapper)
			.list();
	}

	/**
	 * npc 정보를 업데이트 합니다.
	 * @param npc 새로운 npc 정보
	 * @return 업데이트 성공 여부
	 */
	public boolean updateNpc(Npc npc) {
		return jdbcClient.sql(UPDATE_NPC)
			.param("lat", npc.getLat())
			.param("lng", npc.getLng())
			.param("npcSkinId", npc.getNpcSkinId())
			.param("name", npc.getName())
			.param("npcScript", npc.getNpcScript())
			.param("description", npc.getDescription())
			.param("imageUrl", npc.getImageUrl())
			.param("questName", npc.getQuestName())
			.param("npcId", npc.getNpcId())
			.update() > 0;
	}

	/**
	 * npc 를 삭제합니다.
	 * @param npcId npc 아이디
	 * @return 성공 여부
	 */
	public boolean deleteNpc(long npcId) {
		return jdbcClient.sql(DELETE_NPC)
			.param("npcId", npcId)
			.update() > 0;
	}

	public List<NpcMetaDto>  getNpcMetaData(long userId) {
		return jdbcClient.sql(GET_NPC_META_DATA)
			.param("userId", userId)
			.query(npcMetaDtoRowMapper)
			.list();
	}
}
