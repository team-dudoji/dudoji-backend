package com.dudoji.spring.models.dao;

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
             skinId, name, content, imageUrl, price,
            (userPinSkins.userId IS NOT NULL) AS 
            FROM PinSkins
            LEFT OUTER JOIN (
            SELECT skinId, userId
            FROM UserPinSkins
            WHERE userId = :userId
            ) AS userPinSkins
            ON ; 
            """;// TODO: 미완

    private static final String UPDATE_PIN_SKIN = """
            INSERT INTO PinSkin (skinId, name, content, imageUrl, price)
            VALUES (:skinId, :name, :content, :imageUrl, :price)
            ON CONFLICT (skinId) DO UPDATE
            SET name = EXCLUDED.name, content = EXCLUDED.content, imageUrl = EXCLUDED.imageUrl, price = EXCLUDED.price
            """;

    private static final String DELETE_PIN_SKIN = """
            DELETE FROM PinSkin WHERE skinId = :skinId;
            """;

    private static final String UPDATE_USER_PIN_SKIN = """
            INSERT INTO UserPinSkins (skinId, userId)
            VALUES (:skinId, :userId);
            """;

    // CRUD + 기록 남기기

    // read
    public List<PinSkin> getPinSkins(long userId) {
        return jdbcClient.sql(GET_PIN_SKINS)
                .query((rs, rowNum) ->
                    PinSkin.builder()
                            .skinId(rs.getLong("skinId"))
                            .name(rs.getString("name"))
                            .content(rs.getString("content"))
                            .imageUrl(rs.getString("imageUrl"))
                            .price(rs.getInt("price"))
                            .build())
                .list();
    }

    // upsert
    public boolean createOrUpdatePinSkin(PinSkin pinSkin) {
        return jdbcClient.sql(UPDATE_PIN_SKIN)
                .param("skinId", pinSkin.getSkinId())
                .param("name", pinSkin.getName())
                .param("content", pinSkin.getContent())
                .param("imageUrl", pinSkin.getImageUrl())
                .param("price", pinSkin.getPrice())
                .update() > 0;
    }

    // delete
    public boolean deletePinSkin(long skinId) {
        return jdbcClient.sql(DELETE_PIN_SKIN)
                .param("skinId", skinId)
                .update() > 0;
    }

    public boolean updateUserPinSkins(long skinId, long userId) {
        return jdbcClient.sql(UPDATE_USER_PIN_SKIN)
                .param("skinId", skinId)
                .param("userId", userId)
                .update() > 0;
    }
}
