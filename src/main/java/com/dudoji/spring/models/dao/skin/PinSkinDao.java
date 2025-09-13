package com.dudoji.spring.models.dao.skin;

import com.dudoji.spring.dto.skin.PinSkinDto;
import com.dudoji.spring.models.domain.skin.PinSkin;
import com.dudoji.spring.util.BuiltSql;
import com.dudoji.spring.util.SqlBuilder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("PinSkinDao")
@Slf4j
public class PinSkinDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static final Map<String, String> COL_MAP = Map.of(
        "name", "name",
        "skinId", "skinId"
    );

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
            ON ps.skinId = userPinSkins.skinId
            """;

    private static final String GET_PURCHASED_PIN_SKINS = """
        SELECT
          ps.skinId, ps.name, ps.content, ps.imageUrl, ps.price,
          TRUE AS purchased
        FROM PinSkins AS ps
        JOIN UserPinSkins AS ups
          ON ups.skinId = ps.skinId
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

    private static final String GET_ONE_PIN_SKIN = """
            SELECT
              ps.skinId, ps.name, ps.content, ps.imageUrl, ps.price,
              (ups.userId IS NOT NULL) AS purchased
            FROM PinSkins AS ps
            LEFT JOIN UserPinSkins AS ups
              ON ups.skinId = ps.skinId
             AND ups.userId = :userId
            WHERE ps.skinId = :skinId
            LIMIT 1
        """;


    // CRUD + 기록 남기기

    // read
    public List<PinSkinDto> getPinSkins(long userId, int offset, int limit, Sort sort) {
        String baseSql = GET_PIN_SKINS;

        SqlBuilder sqlBuilder = new SqlBuilder();
        sqlBuilder.orderBy(sort, COL_MAP);

        BuiltSql builtSql = sqlBuilder.build();

        String sql = baseSql +
                     builtSql.whereSql() +
                     builtSql.orderBySql() +
                     " LIMIT :limit OFFSET :offset";

        return jdbcClient.sql(sql)
                .param("userId", userId)
                .param("offset", offset)
                .param("limit", limit)
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

    public List<PinSkinDto> getPurchasedPinSkins(long userId, int offset, int limit, Sort sort) {
        String baseSql = GET_PURCHASED_PIN_SKINS;

        SqlBuilder sqlBuilder = new SqlBuilder();
        sqlBuilder.orderBy(sort, COL_MAP);

        BuiltSql builtSql = sqlBuilder.build();

        String sql = baseSql +
            builtSql.whereSql() +
            builtSql.orderBySql() +
            " LIMIT :limit OFFSET :offset";

        return jdbcClient.sql(sql)
            .param("userId", userId)
            .param("offset", offset)
            .param("limit", limit)
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

    public Page<PinSkinDto> getPinSkinsPage(long userId, int offset, int limit, Sort sort) {
        String baseSql = GET_PIN_SKINS;

        SqlBuilder sqlBuilder = new SqlBuilder();
        sqlBuilder.orderBy(sort, COL_MAP);

        BuiltSql builtSql = sqlBuilder.build();

        String sql = baseSql +
            builtSql.whereSql() +
            builtSql.orderBySql() +
            " LIMIT :limit OFFSET :offset";

        Map<String, Object> pageParams = new HashMap<>(builtSql.params());
        pageParams.put("userId", userId);
        pageParams.put("limit", limit);
        pageParams.put("offset", offset);

        List<PinSkinDto> content = jdbcClient.sql(sql)
            .params(pageParams)
            .query((rs, rowNum) -> PinSkinDto.builder()
                .skinId(rs.getLong("skinId"))
                .name(rs.getString("name"))
                .content(rs.getString("content"))
                .imageUrl(rs.getString("imageUrl"))
                .price(rs.getInt("price"))
                .isPurchased(rs.getBoolean("purchased"))
                .build())
            .list();

        String countSql = "SELECT COUNT(*) FROM (" + baseSql + builtSql.whereSql() + ") t";
        Map<String, Object> countParams = new HashMap<>(builtSql.params());
        countParams.put("userId", userId);

        long total = jdbcClient.sql(countSql)
            .params(countParams)
            .query(Long.class)
            .single();

        int pageIndex = limit > 0 ? Math.max(0, offset / limit) : 0;
        Pageable pageable = PageRequest.of(pageIndex, limit, sort == null ? Sort.unsorted() : sort);

        return new PageImpl<>(content, pageable, total);
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

    public PinSkinDto getOnePinSkin(long userId, long skinId) {
        return jdbcClient.sql(GET_ONE_PIN_SKIN)
            .param("userId", userId)
            .param("skinId", skinId)
            .query(
                (rs, rowNum) -> PinSkinDto.builder()
                    .skinId(rs.getLong("skinId"))
                    .name(rs.getString("name"))
                    .content(rs.getString("content"))
                    .imageUrl(rs.getString("imageUrl"))
                    .price(rs.getInt("price"))
                    .isPurchased(rs.getBoolean("purchased"))
                    .build())
            .single();
    }
}
