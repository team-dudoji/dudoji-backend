package com.dudoji.spring.models.dao;

import com.dudoji.spring.dto.PinSkinDto;
import com.dudoji.spring.models.domain.PinSkin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("PinSkinDao")
@Slf4j
public class PinSkinDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static final String GET_PIN_SKINS = """
            SELECT
             ps.skinId, ps.name, ps.content, ps.imageUrl, ps.price,
            (userPinSkins.userId IS NOT NULL) AS purchased 
            FROM PinSkins AS ps
            LEFT OUTER JOIN (
            SELECT skinId, userId
            FROM UserPinSkins
            WHERE userId = :userId
            ) AS userPinSkins
            ON ps.skinId = userPinSkins.skinId; 
            """;

    private static final String UPDATE_PIN_SKIN = """
            INSERT INTO PinSkins (name, content, imageUrl, price)
            VALUES (:name, :content, :imageUrl, :price)
            ON CONFLICT (name) DO UPDATE 
            SET content = EXCLUDED.content, imageUrl = EXCLUDED.imageUrl, price = EXCLUDED.price 
            RETURNING skinId
            """;

    private static final String DELETE_PIN_SKIN = """
            DELETE FROM PinSkins WHERE skinId = :skinId;
            """;

    private static final String UPDATE_USER_PIN_SKIN = """
            INSERT INTO UserPinSkins (skinId, userId)
            VALUES (:skinId, :userId);
            """;

    private static final String GET_PIN_SKIN_BY_ID = """
            SELECT skinId, name, content, imageUrl, price
            FROM PinSkins 
            WHERE skinId = :skinId; 
            """;

    // CRUD + 기록 남기기

    // read
    public List<PinSkinDto> getPinSkins(long userId) {
        return jdbcClient.sql(GET_PIN_SKINS)
                .param("userId", userId)
                .query((rs, rowNum) ->
                    PinSkinDto.builder()
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
    public long createOrUpdatePinSkin(PinSkin pinSkin) {
        return jdbcClient.sql(UPDATE_PIN_SKIN)
                .param("name", pinSkin.getName())
                .param("content", pinSkin.getContent())
                .param("imageUrl", pinSkin.getImageUrl())
                .param("price", pinSkin.getPrice())
                .query(Long.class)
                .single();
    }

    // delete
    public boolean deletePinSkin(long skinId) {
        return jdbcClient.sql(DELETE_PIN_SKIN)
                .param("skinId", skinId)
                .update() > 0;
    }

    // update when buying pinSkinName
    public boolean updateUserPinSkin(long skinId, long userId) {
        return jdbcClient.sql(UPDATE_USER_PIN_SKIN)
                .param("skinId", skinId)
                .param("userId", userId)
                .update() > 0;
    }

    public PinSkin findById(long skinId) {
        return jdbcClient.sql(GET_PIN_SKIN_BY_ID)
                .param("skinId", skinId)
                .query((rs, __) -> PinSkin.builder()
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
