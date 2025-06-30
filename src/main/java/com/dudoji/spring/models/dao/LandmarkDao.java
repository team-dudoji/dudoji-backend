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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LandmarkDao {

    @Autowired
    private DBConnection dbConnection;

    private static final String GET_LANDMARKS = "select * from Landmark";

    public List<Pin> getLandmarks(long userId) {
        List<Pin> pins = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL_PIN_BY_USER_ID);
        ) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long pinId = resultSet.getLong("id");
                    double lat = resultSet.getDouble("lat");
                    double lng = resultSet.getDouble("lng");
                    String content = resultSet.getString("content");
                    String imageUrl = resultSet.getString("image_url");
                    LocalDateTime createdDate = resultSet.getTimestamp("created_at").toLocalDateTime();
                    String placeName = resultSet.getString("placeName");
                    String address = resultSet.getString("address");

                    Pin temp = Pin.builder()
                            .pinId(pinId)
                            .userId(userId)
                            .lat(lat)
                            .lng(lng)
                            .content(content)
                            .createdDate(createdDate)
                            .imageUrl(imageUrl)
                            .placeName(placeName)
                            .address(address)
                            .build();

                    pins.add(temp);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return pins;
    }
}
