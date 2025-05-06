package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.Pin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class PinDao {

    @Autowired
    private DBConnection dbConnection;

    private static final String CREATE_PIN_BY_REQUEST = "INSERT INTO pin (userId, lat, lng, title, content) VALUES (?, ?, ?, ?, ?)";

    private static final String GET_CLOSE_PIN_BY_MIN_MAX = "SELECT pinId, userId, lat, lng, title, content, createDate " +
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
            statement.setString(4, pin.getTitle());
            statement.setString(5, pin.getContent());
            statement.execute();
            log.info("Pin created");
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
                Long pinId = resultSet.getLong("pinId");
                Long userId = resultSet.getLong("userId");
                double lat = resultSet.getDouble("lat");
                double lng = resultSet.getDouble("lng");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                Date createDate = resultSet.getDate("createDate");

                Pin temp = Pin.builder()
                        .pinId(pinId)
                        .userId(userId)
                        .lat(lat)
                        .lng(lng)
                        .title(title)
                        .content(content)
                        .createdDate(createDate)
                        .build();

                pins.add(temp);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return pins;
    }
}
