package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.Pin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository("PinDao")
public class PinDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static final String CREATE_PIN_BY_REQUEST = "INSERT INTO pin (user_id, lat, lng, content, created_at, image_url, placeName, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    private static final String GET_CLOSE_PIN_BY_MIN_MAX = "SELECT id, user_id, lat, lng, content, created_at, image_url, placeName, address " +
            "FROM pin " +
            "WHERE lat BETWEEN ? AND ? " +
            "AND lng BETWEEN ? AND ?";

    private static final String GET_ALL_PIN_BY_USER_ID = "SELECT id, lat, lng, content, created_at, image_url, placeName, address " +
            "FROM pin " +
            "WHERE user_id = ?";

    private static final String GET_NUM_OF_PIN_BY_USER_ID = """
            SELECT count(1)
            FROM pin
            WHERE user_id = :userId;
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
                                .userId(rs.getLong("user_id"))
                                .lat(rs.getDouble("lat"))
                                .lng(rs.getDouble("lng"))
                                .content(rs.getString("content"))
                                .imageUrl(rs.getString("image_url"))
                                .createdDate(rs.getTimestamp("created_at").toLocalDateTime())
                                .placeName(rs.getString("placeName"))
                                .address(rs.getString("address"))
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
                            .imageUrl(rs.getString("image_url"))
                            .createdDate(
                                    rs.getTimestamp("created_at").toLocalDateTime())
                            .placeName(rs.getString("placeName"))
                            .address(rs.getString("address"))
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
    // TODO: 삭제 기능 만들 것
}
