package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.domain.Pin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Slf4j
@Repository("PinDao")
public class PinDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static final String CREATE_PIN_BY_REQUEST = "INSERT INTO pin (userId, lat, lng, content, createdAt, imageUrl, placeName, address, skinid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    private static final String GET_CLOSE_PIN_BY_MIN_MAX = "SELECT id, userId, lat, lng, content, createdAt, imageUrl, placeName, address, skinid " +
            "FROM pin " +
            "WHERE lat BETWEEN ? AND ? " +
            "AND lng BETWEEN ? AND ?";

    private static final String GET_ALL_PIN_BY_USER_ID = "SELECT id, lat, lng, content, createdAt, imageUrl, placeName, address, skinid " +
            "FROM pin " +
            "WHERE userId = ?";

    private static final String GET_NUM_OF_PIN_BY_USER_ID = """
            SELECT count(1)
            FROM pin
            WHERE userId = :userId;
            """;

    private static final String GET_NUM_OF_PIN_BY_USER_ID_AND_DATE = """
            SELECT count(1)
            FROM pin
            WHERE
            userId = :userId AND
            createdAt >= :startDate AND
            createdAt < :endDate
            """;
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
            double maxLat, double maxLng) {

        return jdbcClient.sql(GET_CLOSE_PIN_BY_MIN_MAX)
                .param(minLat)
                .param(maxLat)
                .param(minLng)
                .param(maxLng)
                .query((rs, rowNum) ->
                        Pin.builder()
                                .pinId(rs.getLong("id"))
                                .userId(rs.getLong("userId"))
                                .lat(rs.getDouble("lat"))
                                .lng(rs.getDouble("lng"))
                                .content(rs.getString("content"))
                                .imageUrl(rs.getString("imageUrl"))
                                .createdDate(rs.getTimestamp("createdAt").toLocalDateTime())
                                .placeName(rs.getString("placeName"))
                                .address(rs.getString("address"))
                                .pinSkinId(rs.getLong("skinid"))
                                .build())
                .list();
    }

    public List<Pin> getALlPinsByUserId(long userId) {
        return jdbcClient.sql(GET_ALL_PIN_BY_USER_ID)
                .param(userId)
                .query((rs, rowNum) ->
                        Pin.builder()
                            .pinId(rs.getLong("id"))
                            .userId(userId)
                            .lat(rs.getDouble("lat"))
                            .lng(rs.getDouble("lng"))
                            .content(rs.getString("content"))
                            .imageUrl(rs.getString("imageUrl"))
                            .createdDate(
                                    rs.getTimestamp("createdAt").toLocalDateTime())
                            .placeName(rs.getString("placeName"))
                            .address(rs.getString("address"))
                            .pinSkinId(rs.getLong("skinid"))
                            .build())
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
