package com.dudoji.spring.models.dao;

import com.dudoji.spring.dto.UserWalkDistancesDto;
import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.UserWalkDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class UserWalkDistanceDao {

    @Autowired
    private DBConnection dbConnection;

    // SQL Query String
    private static String GET_DISTANCE_BY_ID_AND_DATE = "SELECT * FROM user_walk_distance WHERE user_id=? AND distance_date=?";

    private static String GET_DISTANCE_BY_ID_AND_DURATION = "SELECT * FROM user_walk_distance WHERE user_id=? AND distance_date BETWEEN ? AND ? ORDER BY distance_date";

    private static String CREATE_DISTANCE_BY_ID_AND_DATE = "INSERT INTO user_walk_distance (user_id, distance_date, distance_meter) VALUES (?, ?, ?)" +
            "ON CONFLICT (user_id, distance_date) DO UPDATE SET distance_meter=?";



    /**
     * Get User Step Info by user id and distanceDate
     * @param uid user id
     * @param distanceDate distanceDate which we want to know step count
     * @return UserWalkDistanceDto Object Created with uid, distanceDate
     */
    @Deprecated(since = "No Using Single Day")
    public UserWalkDistance getUserWalkDistanceByIdOnDate(long uid, LocalDate distanceDate) {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_DISTANCE_BY_ID_AND_DATE);
        ) {

            preparedStatement.setLong(1, uid);
            preparedStatement.setDate(2, java.sql.Date.valueOf(distanceDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long stepId = resultSet.getLong("id");
                    int distanceMeter = resultSet.getInt("distance_meter");
                    return UserWalkDistance.builder().uid(uid).stepId(stepId).distance_meter(distanceMeter).distance_date(distanceDate).build();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public UserWalkDistancesDto getUserWalkDistanceByIdOnDuration(long uid, LocalDate startDate, LocalDate endDate) {
        UserWalkDistancesDto result = new UserWalkDistancesDto();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_DISTANCE_BY_ID_AND_DURATION);
        ) {


            preparedStatement.setLong(1, uid);
            preparedStatement.setDate(2, java.sql.Date.valueOf(String.valueOf(startDate)));
            preparedStatement.setDate(3, java.sql.Date.valueOf(String.valueOf(endDate)));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int distanceMeter = resultSet.getInt("distance_meter");
                    LocalDate distanceDate = resultSet.getDate("distance_date").toLocalDate();
                    result.userWalkDistances.add(UserWalkDistancesDto.UserWalkDistanceDto.builder().distance(distanceMeter).date(distanceDate).build());
                }
            }

            return result;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create User Step tuple with id, distanceDate, distanceMeter. If existed, update.
     * @param uid user id
     * @param distanceDate target distanceDate
     * @param distanceMeter user's step count
     * @return True when query exactly play.
     */
    public boolean createUserWalkDistance(long uid, LocalDate distanceDate, int distanceMeter) {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_DISTANCE_BY_ID_AND_DATE);
        ) {

            preparedStatement.setLong(1, uid);
            preparedStatement.setDate(2, java.sql.Date.valueOf(distanceDate));
            preparedStatement.setInt(3, distanceMeter);
            preparedStatement.setInt(4, distanceMeter);
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
