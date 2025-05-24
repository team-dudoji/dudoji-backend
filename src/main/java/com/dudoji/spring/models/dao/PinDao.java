package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.Pin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class PinDao {

    @Autowired
    private DBConnection dbConnection;

    private static final String CREATE_PIN_BY_REQUEST = "INSERT INTO pin (user_id, lat, lng, content, created_at, image_url) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String GET_CLOSE_PIN_BY_MIN_MAX = "SELECT id, user_id, lat, lng, content, created_at " +
            "FROM pin " +
            "WHERE lat BETWEEN ? AND ? " +
            "AND lng BETWEEN ? AND ?";


    /**
     * Create New Pin Content
     *
     * @param pin Pin Object which wants create
     */
    public void createPin(Pin pin) {
        try (Connection connection = dbConnection.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(CREATE_PIN_BY_REQUEST);
            statement.setLong(1, pin.getUserId());
            statement.setDouble(2, pin.getLat());
            statement.setDouble(3, pin.getLng());
            statement.setString(4, pin.getContent());
            statement.setObject(5, pin.getCreatedDate());
            statement.setString(6, pin.getImageUrl());
            statement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        List<Pin> pins = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(GET_CLOSE_PIN_BY_MIN_MAX);
            statement.setDouble(1, minLat);
            statement.setDouble(2, maxLat);
            statement.setDouble(3, minLng);
            statement.setDouble(4, maxLng);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long pinId = resultSet.getLong("id");
                Long userId = resultSet.getLong("user_id");
                double lat = resultSet.getDouble("lat");
                double lng = resultSet.getDouble("lng");
                String content = resultSet.getString("content");
                LocalDateTime createdDate = resultSet.getTimestamp("created_at").toLocalDateTime();

                Pin temp = Pin.builder()
                        .pinId(pinId)
                        .userId(userId)
                        .lat(lat)
                        .lng(lng)
                        .content(content)
                        .createdDate(createdDate)
                        .build();

                pins.add(temp);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return pins;
    }
}
