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

    @Deprecated
    @Autowired
    private DBConnection dbConnection;

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
//        List<Pin> pins = new ArrayList<>();
//
//        try (Connection connection = dbConnection.getConnection();
//             PreparedStatement statement = connection.prepareStatement(GET_CLOSE_PIN_BY_MIN_MAX);
//        ) {
//            statement.setDouble(1, minLat);
//            statement.setDouble(2, maxLat);
//            statement.setDouble(3, minLng);
//            statement.setDouble(4, maxLng);
//            try (ResultSet resultSet = statement.executeQuery()) {
//                while (resultSet.next()) {
//                    Long pinId = resultSet.getLong("id");
//                    Long userId = resultSet.getLong("user_id");
//                    double lat = resultSet.getDouble("lat");
//                    double lng = resultSet.getDouble("lng");
//                    String content = resultSet.getString("content");
//                    String imageUrl = resultSet.getString("image_url");
//                    LocalDateTime createdDate = resultSet.getTimestamp("created_at").toLocalDateTime();
//                    String placeName = resultSet.getString("placeName");
//                    String address = resultSet.getString("address");
//
//                    Pin temp = Pin.builder()
//                            .pinId(pinId)
//                            .userId(userId)
//                            .lat(lat)
//                            .lng(lng)
//                            .content(content)
//                            .createdDate(createdDate)
//                            .imageUrl(imageUrl)
//                            .placeName(placeName)
//                            .address(address)
//                            .build();
//
//                    pins.add(temp);
//                }
//            }
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        return pins;
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
//        List<Pin> pins = new ArrayList<>();
//
//        try (Connection connection = dbConnection.getConnection();
//             PreparedStatement statement = connection.prepareStatement(GET_ALL_PIN_BY_USER_ID);
//        ) {
//            statement.setLong(1, userId);
//            try (ResultSet resultSet = statement.executeQuery()) {
//                while (resultSet.next()) {
//                    Long pinId = resultSet.getLong("id");
//                    double lat = resultSet.getDouble("lat");
//                    double lng = resultSet.getDouble("lng");
//                    String content = resultSet.getString("content");
//                    String imageUrl = resultSet.getString("image_url");
//                    LocalDateTime createdDate = resultSet.getTimestamp("created_at").toLocalDateTime();
//                    String placeName = resultSet.getString("placeName");
//                    String address = resultSet.getString("address");
//
//                    Pin temp = Pin.builder()
//                            .pinId(pinId)
//                            .userId(userId)
//                            .lat(lat)
//                            .lng(lng)
//                            .content(content)
//                            .createdDate(createdDate)
//                            .imageUrl(imageUrl)
//                            .placeName(placeName)
//                            .address(address)
//                            .build();
//
//                    pins.add(temp);
//                }
//            }
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        return pins;
    }
}
