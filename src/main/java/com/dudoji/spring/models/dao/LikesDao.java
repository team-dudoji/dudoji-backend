package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Repository("LikesDao")
public class LikesDao {

    private static final String LIKE_PIN_BY_ID = "INSERT INTO likes (user_id, pin_id) VALUES (?, ?)";
    private static final String UNLIKE_PIN_BY_ID = "DELETE FROM likes WHERE user_id = ? AND pin_id = ?";
    private static final String GET_LIKE_COUNT_BY_ID = "SELECT like_count FROM like_counts WHERE pin_id = ?";
    private static final String GET_IS_LIKED_BY_ID = "SELECT EXISTS (\n" +
            "    SELECT 1 FROM likes WHERE pin_id = ? AND user_id = ?\n" +
            ")";
    private static final String REFRESH_LIKES = "REFRESH MATERIALIZED VIEW like_counts";

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
}
