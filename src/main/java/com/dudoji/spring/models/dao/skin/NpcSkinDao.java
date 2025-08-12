package com.dudoji.spring.models.dao.skin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.dudoji.spring.models.domain.skin.NpcSkin;

@Repository("NpcSkinDao")
public class NpcSkinDao {
	@Autowired
	private JdbcClient jdbcClient;

	private static final String INSERT_NPC_SKIN = """
		INSERT INTO NpcSkin (imageUrl, regionId) VALUES
		(:imageUrl, :regionId)
		RETURNING npcSkinId;
		""";

	private static final String GET_NPC_SKIN_BY_ID = """
		SELECT * FROM NpcSkin WHERE npcSkinId = :npcSkinId
		""";

	private static final String GET_ALL_NPC_SKIN = """
		SELECT * FROM NpcSkin
		""";

	private static final String GET_NPC_SKIN_BY_REGION = """
		SELECT * FROM NpcSkin WHERE regionId = :regionId
		""";

	private static final String GET_NPC_SKIN_BY_IDS = """
		SELECT * FROM NpcSkin WHERE NpcSkinId IN (:npcSkinIds)
		""";

	private static final String UPDATE_NPC_SKIN = """
		UPDATE NpcSkin SET imageUrl = :imageUrl, regionId = :regionId
		WHERE npcSkinId = :npcSkinId
		""";

	private final RowMapper<NpcSkin> npcSkinRowMapper = (rs, rowNum) -> {
		return new NpcSkin(
			rs.getLong("npcSkinId"),
			rs.getString("imageUrl"),
			rs.getInt("regionId")
		);
	};

	private static final String DELETE_NPC_SKIN = """
		DELETE FROM NpcSkin WHERE npcSkinId = :npcSkinId
		""";

	/**
	 * npc 스킨을 생성하고 해당하는 아이디를 반환합니다
	 * @param npcSkin 스킨 정보
	 * @return long id
	 */
	public long createNpcSkin(NpcSkin npcSkin) {
		return jdbcClient
			.sql(INSERT_NPC_SKIN)
			.param("imageUrl", npcSkin.getImageUrl())
			.param("regionId",  npcSkin.getRegionId())
			.query(Long.class)
			.single();
	}

	/**
	 * 스킨 아이디를 가지고 한 개의 스킨을 조회합니다
	 * @param npcSkinId 원하는 스킨 아이디
	 * @return NpcSkin 오브젝트
	 */
	public NpcSkin getNpcSkinById(long npcSkinId) {
		return jdbcClient
			.sql(GET_NPC_SKIN_BY_ID)
			.param("npcSkinId", npcSkinId)
			.query(npcSkinRowMapper)
			.single();
	}

	/**
	 * 지역에 있는 모든 NpcSkin 을 가져옵니다.
	 * @param regionId 지역 아이디
	 * @return Npc Skin List
	 */
	public List<NpcSkin> getNpcSkinByRegion(long regionId) {
		return jdbcClient
			.sql(GET_NPC_SKIN_BY_REGION)
			.param("regionId", regionId)
			.query(npcSkinRowMapper)
			.list();
	}

	/**
	 * NpcSkin 을 업데이트 합니다.
	 * @param npcSkin 업데이트 하고 싶은 객체
	 * @return 업데이트 성공 여부
	 */
	public boolean updateNpcSkin(NpcSkin npcSkin) {
		return jdbcClient
			.sql(UPDATE_NPC_SKIN)
			.param("npcSkinId", npcSkin.getNpcSkinId())
			.param("imageUrl",   npcSkin.getImageUrl())
			.param("regionId",   npcSkin.getRegionId())
			.update() > 0;  // 반영된 row 수 리턴
	}

	/**
	 * NpcSkin 을 삭제합니다.
	 * @param npcSkinId 삭제하고자 하는 스킨 id
	 * @return 삭제 성공 여부
	 */
	public boolean deleteNpcSkin(long npcSkinId) {
		return jdbcClient
			.sql(DELETE_NPC_SKIN)
			.param("npcSkinId", npcSkinId)
			.update() > 0;  // 반영된 row 수 리턴
	}

	/**
	 * 벌킹으로 스킨 데이터를 가져옵니다.
	 * @param npcSkinIds npc Skin id 목록
	 * @return npc skin list
	 */
	public List<NpcSkin> getNpcSkinByIds(List<Long> npcSkinIds) {
		return jdbcClient.sql(GET_NPC_SKIN_BY_IDS)
			.param("npcSkinIds", npcSkinIds)
			.query(npcSkinRowMapper)
			.list();
	}

	public List<NpcSkin> getAllNpcSkins() {
		return jdbcClient.sql(GET_ALL_NPC_SKIN)
			.query(npcSkinRowMapper)
			.list();
	}
}
