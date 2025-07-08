package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.domain.Landmark;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
public class LandmarkDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static final String GET_LANDMARKS = """
       SELECT
          Landmark.landmarkId as landmarkId, lat, lng, placeName, content, imageUrl, address,
          (ld.userId IS NOT NULL) AS isDetected
        FROM Landmark
        LEFT OUTER JOIN (
          SELECT landmarkId, userId
          FROM landmarkDetection
          WHERE userId = :userId
        ) AS ld
        ON Landmark.landmarkId = ld.landmarkId
       """;

    private static final String SAVE_LANDMARK = """
            INSERT INTO Landmark(lat, lng, placeName, content, imageUrl, address)
            VALUES
            (:lat, :lng, :placeName, :content, :imageUrl, :address);
            """;
    private static final String SAVE_LANDMARK_DETECTION = """
            INSERT INTO landmarkDetection(landmarkId, userId)
            VALUES
            (:landmarkId, :userId);
            """;

    private static final String DELETE_LANDMARK = """
            DELETE FROM Landmark WHERE landmarkId = :landmarkId;
            """;

    private static final String UPDATE_LANDMARK = """
            UPDATE Landmark
            SET lat = :lat, lng = :lng, placeName = :placeName, content = :content, imageUrl = :imageUrl, address = :address
            WHERE landmarkId = :landmarkId;
            """;

    public List<Landmark> getLandmarks(long userId) {
        return jdbcClient.sql(GET_LANDMARKS)
                .param("userId", userId)
                .query((rs, numOfRow) ->
                        new Landmark(
                                rs.getLong("landmarkId"),
                                rs.getDouble("lat"),
                                rs.getDouble("lng"),
                                rs.getString("content"),
                                rs.getString("imageUrl"),
                                rs.getString("placeName"),
                                rs.getString("address"),
                                rs.getBoolean("isDetected")
                        ))
                .list();
    }

    public void saveLandmark(double lat, double lng, String content, String imageUrl, String placeName, String address) {
        jdbcClient.sql(SAVE_LANDMARK)
                .param("lat", lat)
                .param("lng", lng)
                .param("content", content)
                .param("imageUrl", imageUrl)
                .param("placeName", placeName)
                .param("address", address)
                .update();
    }

    public void setDetect(long userId, long landmarkId) {
        jdbcClient.sql(SAVE_LANDMARK_DETECTION)
                .param("userId", userId)
                .param("landmarkId", landmarkId)
                .update();
    }

    public void deleteLandmark(Long landmarkId) {
        jdbcClient.sql(DELETE_LANDMARK)
                .param("landmarkId", landmarkId)
                .update();
    }

    public void putLandmark(Landmark landmark) {
        jdbcClient.sql(UPDATE_LANDMARK)
                .param("landmarkId", landmark.getLandmarkId())
                .param("lat", landmark.getLat())
                .param("lng", landmark.getLng())
                .param("content", landmark.getContent())
                .param("imageUrl", landmark.getImageUrl())
                .param("placeName", landmark.getPlaceName())
                .param("address", landmark.getAddress())
                .update();
    }
}
