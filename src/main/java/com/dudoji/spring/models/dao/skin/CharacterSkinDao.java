package com.dudoji.spring.models.dao.skin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.dudoji.spring.dto.skin.CharacterSkinDto;
import com.dudoji.spring.models.domain.skin.CharacterSkin;

@Repository("CharacterSkinDao")
public class CharacterSkinDao {

	@Autowired
	private JdbcClient jdbcClient;

	private static final String GET_CHARACTER_SKINS = """
            SELECT
             cs.skinId, cs.name, cs.content, cs.imageUrl, cs.price,
            (userCharacterSkins.userId IS NOT NULL) AS purchased
            FROM CharacterSkins AS cs
            LEFT OUTER JOIN (
            SELECT skinId, userId
            FROM UserCharacterSkins
            WHERE userId = :userId
            ) AS userCharacterSkins
            ON cs.skinId = userCharacterSkins.skinId;
            """;

	private static final String UPDATE_CHARACTER_SKIN = """
            INSERT INTO CharacterSkins (name, content, imageUrl, price)
            VALUES (:name, :content, :imageUrl, :price)
            ON CONFLICT (name) DO UPDATE
            SET content = EXCLUDED.content, imageUrl = EXCLUDED.imageUrl, price = EXCLUDED.price
            RETURNING skinId
            """;

	private static final String DELETE_CHARACTER_SKIN = """
            DELETE FROM CharacterSkins WHERE skinId = :skinId;
            """;

	private static final String UPDATE_USER_CHARACTER_SKIN = """
            INSERT INTO UserCharacterSkins (skinId, userId)
            VALUES (:skinId, :userId);
            """;

	private static final String GET_CHARACTER_SKIN_BY_ID = """
            SELECT skinId, name, content, imageUrl, price
            FROM CharacterSkins
            WHERE skinId = :skinId;
            """;


	public List<CharacterSkinDto> getCharacterSkins(long userId) {
		return jdbcClient.sql(GET_CHARACTER_SKINS)
			.param("userId", userId)
			.query((rs, rowNum) ->
				CharacterSkinDto.builder()
					.skinId(rs.getLong("skinId"))
					.name(rs.getString("name"))
					.content(rs.getString("content"))
					.imageUrl(rs.getString("imageUrl"))
					.price(rs.getInt("price"))
					.isPurchased(rs.getBoolean("purchased"))
					.build())
			.list();
	}

	// upsert
	public long createOrUpdateCharacterSkin(CharacterSkin pinSkin) {
		return jdbcClient.sql(UPDATE_CHARACTER_SKIN)
			.param("name", pinSkin.getName())
			.param("content", pinSkin.getContent())
			.param("imageUrl", pinSkin.getImageUrl())
			.param("price", pinSkin.getPrice())
			.query(Long.class)
			.single();
	}

	// delete
	public boolean deleteCharacterSkin(long skinId) {
		return jdbcClient.sql(DELETE_CHARACTER_SKIN)
			.param("skinId", skinId)
			.update() > 0;
	}

	// update when buying pinSkinName
	public boolean updateUserCharacterSkin(long skinId, long userId) {
		return jdbcClient.sql(UPDATE_USER_CHARACTER_SKIN)
			.param("skinId", skinId)
			.param("userId", userId)
			.update() > 0;
	}

	public CharacterSkin findById(long skinId) {
		return jdbcClient.sql(GET_CHARACTER_SKIN_BY_ID)
			.param("skinId", skinId)
			.query((rs, __) -> CharacterSkin.builder()
				.skinId(rs.getLong("skinId"))
				.name(rs.getString("name"))
				.content(rs.getString("content"))
				.imageUrl(rs.getString("imageUrl"))
				.price(rs.getInt("price"))
				.build())
			.optional()
			.orElse(null);
	}
}
