package com.dudoji.spring.models.dao;

import com.dudoji.spring.dto.UserWalkDistancesDto;
import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.UserWalkDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository("UserWalkDistanceDao")
public class UserWalkDistanceDao {

    @Deprecated
    @Autowired
    private DBConnection dbConnection;

    @Autowired
    private JdbcClient jdbcClient;

    private static String GET_DISTANCE_BY_ID_AND_DURATION = "SELECT * FROM user_walk_distance WHERE user_id=? AND distance_date BETWEEN ? AND ? ORDER BY distance_date";

    private static String CREATE_DISTANCE_BY_ID_AND_DATE = "INSERT INTO user_walk_distance (user_id, distance_date, distance_meter) VALUES (?, ?, ?)" +
            "ON CONFLICT (user_id, distance_date) DO UPDATE SET distance_meter=?";


    public UserWalkDistancesDto getUserWalkDistanceByIdOnDuration(long uid, LocalDate startDate, LocalDate endDate) {
        UserWalkDistancesDto result = new UserWalkDistancesDto();

        result.userWalkDistances.addAll(
                jdbcClient.sql(GET_DISTANCE_BY_ID_AND_DURATION)
                        .param(uid)
                        .param(java.sql.Date.valueOf(startDate))
                        .param(java.sql.Date.valueOf(endDate))
                        .query((rs, rowNum) ->
                                UserWalkDistancesDto.UserWalkDistanceDto.builder()
                                        .distance(rs.getInt("distance_meter"))
                                        .date(rs.getDate("distance_date").toLocalDate())
                                        .build())
                        .list()
        );
        return result;
//        try (Connection connection = dbConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(GET_DISTANCE_BY_ID_AND_DURATION);
//        ) {
//
//
//            preparedStatement.setLong(1, uid);
//            preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
//            preparedStatement.setDate(3, java.sql.Date.valueOf(endDate));
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                while (resultSet.next()) {
//                    int distanceMeter = resultSet.getInt("distance_meter");
//                    LocalDate distanceDate = resultSet.getDate("distance_date").toLocalDate();
//                    result.userWalkDistances.add(UserWalkDistancesDto.UserWalkDistanceDto.builder().distance(distanceMeter).date(distanceDate).build());
//                }
//            }
//
//            return result;
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Create User Step tuple with id, distanceDate, distanceMeter. If existed, update.
     * @param uid user id
     * @param distanceDate target distanceDate
     * @param distanceMeter user's step count
     * @return True when query exactly play.
     */
    public boolean createUserWalkDistance(long uid, LocalDate distanceDate, int distanceMeter) {

        boolean updated =
                jdbcClient.sql(CREATE_DISTANCE_BY_ID_AND_DATE)
                        .param(uid)
                        .param(java.sql.Date.valueOf(distanceDate))
                        .param(distanceMeter)
                        .param(distanceMeter)
                        .query(Boolean.class)
                        .single();

        return updated;

//        try (Connection connection = dbConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_DISTANCE_BY_ID_AND_DATE);
//        ) {
//
//            preparedStatement.setLong(1, uid);
//            preparedStatement.setDate(2, java.sql.Date.valueOf(distanceDate));
//            preparedStatement.setInt(3, distanceMeter);
//            preparedStatement.setInt(4, distanceMeter);
//            preparedStatement.executeUpdate();
//
//            return true;
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }
}
