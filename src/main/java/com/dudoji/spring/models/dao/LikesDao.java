package com.dudoji.spring.models.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("LikesDao")
public class LikesDao {

    private static final String LIKE_PIN_BY_ID = "INSERT INTO likes (userId, pinId) VALUES (?, ?)";
    private static final String UNLIKE_PIN_BY_ID = "DELETE FROM likes WHERE userId = ? AND pinId = ?";
    private static final String GET_LIKE_COUNT_BY_ID = "SELECT likeCount FROM likeCounts WHERE pinId = ?";
    private static final String GET_IS_LIKED_BY_ID = "SELECT EXISTS (\n" +
            "    SELECT 1 FROM likes WHERE pinId = ? AND userId = ?\n" +
            ")";
    private static final String REFRESH_LIKES = "REFRESH MATERIALIZED VIEW likeCounts";
    private static final String GET_LIKE_PIN_ID_SET = """
        SELECT pinId FROM likes
        WHERE userId = :userId
        AND pinId IN (:pinIds)
        """;

    @Autowired
    private JdbcClient jdbcClient;

    public boolean likePin(long userId, long pinId) {
        return jdbcClient.sql(LIKE_PIN_BY_ID)
                .param(userId)
                .param(pinId)
                .update() > 0;
    }

    public boolean unlikePin(long userId, long pinId) {
        return jdbcClient.sql(UNLIKE_PIN_BY_ID)
                .param(userId)
                .param(pinId)
                .update() > 0;
    }

    public int getLikesCount(long pinId) {
        return jdbcClient.sql(GET_LIKE_COUNT_BY_ID)
                .param(pinId)
                .query(Integer.class)
                .optional()
                .orElse(0);
    }

    public boolean isLiked(long userId, long pinId) {
        return jdbcClient.sql(GET_IS_LIKED_BY_ID)
                .param(pinId)
                .param(userId)
                .query(Boolean.class)
                .optional()
                .orElse(false);
    }

    public void refreshViews() {
        jdbcClient.sql(REFRESH_LIKES)
                .update();
    }

    public Set<Long> getLikedSet(long userId, List<Long> pinIds) {
        List<Long> likedList = jdbcClient.sql(GET_LIKE_PIN_ID_SET)
            .param("userId", userId)
            .param("pinIds", pinIds)
            .query( (rs, rowNum) ->
                rs.getLong("pinId")
            )
            .list();

        return new HashSet<>(likedList);
    }
}
