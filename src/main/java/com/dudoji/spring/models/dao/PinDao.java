package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.domain.Pin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Slf4j
@Repository("PinDao")
public class PinDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static final String CREATE_PIN_BY_REQUEST = "INSERT INTO Pin (userId, lat, lng, content, createdAt, imageUrl, placeName, address, skinid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    // private static final String GET_CLOSE_PIN_BY_MIN_MAX_WITH_LIMIT = "SELECT id, userId, lat, lng, content, createdAt, imageUrl, placeName, address, skinid " +
    //         "FROM Pin " +
    //         "WHERE lat BETWEEN ? AND ? " +
    //         "AND lng BETWEEN ? AND ? " +
    //         "LIMIT ? OFFSET ?";

    private static final String GET_CLOSE_PIN_BY_MIN_MAX_WITH_LIMIT = """
        SELECT p.id, p.userId, p.lat, p.lng, p.content, p.createdAt, p.imageUrl, p.placeName, p.address, p.skinId, lc.likecount
        FROM Pin AS p 
        LEFT JOIN likecounts lc ON p.id = lc.pinId 
        WHERE p.lat BETWEEN ? AND ? 
        AND p.lng BETWEEN ? AND ? 
        ORDER BY lc.likecount DESC NULLS LAST
        LIMIT ? OFFSET ?
    """;

    // private static final String GET_ALL_PIN_BY_USER_ID_WITH_LIMIT = "SELECT id, userId, lat, lng, content, createdAt, imageUrl, placeName, address, skinid " +
    //         "FROM Pin " +
    //         "WHERE userId = ? " +
    //         "LIMIT ? OFFSET ?";

    private static final String GET_ALL_PIN_BY_USER_ID_WITH_LIMIT = """
        SELECT p.id, p.userId, p.lat, p.lng, p.content, p.createdAt, p.imageUrl, p.placeName, p.address, p.skinId, lc.likecount
        FROM Pin AS p
        LEFT JOIN likecounts lc ON p.id = lc.pinId 
        WHERE userId = ? 
        ORDER BY lc.likecount DESC NULLS LAST
        LIMIT ? OFFSET ?
    """;

    private static final String GET_NUM_OF_PIN_BY_USER_ID = """
            SELECT count(1)
            FROM Pin
            WHERE userId = :userId;
            """;

    private static final String GET_NUM_OF_PIN_BY_USER_ID_AND_DATE = """
            SELECT count(1)
            FROM Pin
            WHERE
            userId = :userId AND
            createdAt >= :startDate AND
            createdAt < :endDate
            """;

    private final RowMapper<Pin> PinMapper = (rs, rowNum) -> new Pin(
        rs.getDouble("lat"),
        rs.getDouble("lng"),
        rs.getLong("id"),
        rs.getLong("userId"),
        rs.getTimestamp("createdAt").toLocalDateTime(),
        rs.getString("content"),
        rs.getString("imageUrl"),
        rs.getString("placeName"),
        rs.getString("address"),
        rs.getLong("skinId"),
        rs.getInt("likecount")
    );

    /**
     * Create New Pin Content
     *
     * @param pin Pin Object which wants create
     */
    public long createPin(Pin pin) {
        return jdbcClient.sql(CREATE_PIN_BY_REQUEST)
                .param(pin.getUserId())
                .param(pin.getLat())
                .param(pin.getLng())
                .param(pin.getContent())
                .param(pin.getCreatedDate())
                .param(pin.getImageUrl())
                .param(pin.getPlaceName())
                .param(pin.getAddress())
                .param(pin.getPinSkinId())
                .query(Long.class)
                .single();
    }

    /**
     * Retrieves a list of Pin objects within the specified lat/lng bound.
     * @param minLat minimum lat of the search area
     * @param minLng minimum lng of the search area
     * @param maxLat maximum lat of the search area
     * @param maxLng maximum lng of the search area
     * @return a list of Pin instances located within the given range
     */
    public List<Pin> getClosePins(
            double minLat, double minLng,
            double maxLat, double maxLng,
        int limit, int offset) {

        return jdbcClient.sql(GET_CLOSE_PIN_BY_MIN_MAX_WITH_LIMIT)
                .param(minLat)
                .param(maxLat)
                .param(minLng)
                .param(maxLng)
                .param(limit)
                .param(offset)
                .query(PinMapper)
                .list();
    }

    public List<Pin> getALlPinsByUserId(long userId, int limit, int offset) {
        return jdbcClient.sql(GET_ALL_PIN_BY_USER_ID_WITH_LIMIT)
                .param(userId)
                .param(limit)
                .param(offset)
                .query(PinMapper)
                .list();
    }

    public int getNumOfPinByUserId(long userId) {
        Long num = (Long) jdbcClient.sql(GET_NUM_OF_PIN_BY_USER_ID)
                .param("userId", userId)
                .query()
                .singleValue();
        return num.intValue();
    }

    public int getNumOfPinByUserIdAndDates(long userId, Date startDate, Date endDate) {
        Long num = (Long) jdbcClient.sql(GET_NUM_OF_PIN_BY_USER_ID_AND_DATE)
                .param("userId", userId)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .query()
                .singleValue();
        return num.intValue();
    }
    // TODO: 삭제 기능 만들 것
}
