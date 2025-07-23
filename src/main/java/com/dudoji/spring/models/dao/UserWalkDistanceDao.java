package com.dudoji.spring.models.dao;

import com.dudoji.spring.dto.UserWalkDistancesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository("UserWalkDistanceDao")
public class UserWalkDistanceDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static final String GET_DISTANCE_BY_ID_AND_DURATION = "SELECT * FROM userWalkDistance WHERE userId=? AND distanceDate BETWEEN ? AND ? ORDER BY distanceDate";

    private static final String CREATE_DISTANCE_BY_ID_AND_DATE = "INSERT INTO userWalkDistance (userId, distanceDate, distanceMeter) VALUES (?, ?, ?)" +
            "ON CONFLICT (userId, distanceDate) DO UPDATE SET distanceMeter=?";

    private static final String DELETE_DISTANCE_BY_ID_AND_DURATION = "DELETE FROM userWalkDistance WHERE userId=? AND distanceDate BETWEEN ? AND ?";

    private static final String GET_TOTAL_DISTANCE_BY_USER_ID = """
        SELECT COALESCE(SUM(distanceMeter), 0) AS totalDistance
        FROM user_walk_distance
        WHERE userId = :userId;
        """;


    public UserWalkDistancesDto getUserWalkDistanceByIdOnDuration(long uid, LocalDate startDate, LocalDate endDate) {
        UserWalkDistancesDto result = new UserWalkDistancesDto();

        result.userWalkDistances.addAll(
                jdbcClient.sql(GET_DISTANCE_BY_ID_AND_DURATION)
                        .param(uid)
                        .param(java.sql.Date.valueOf(startDate))
                        .param(java.sql.Date.valueOf(endDate))
                        .query((rs, rowNum) ->
                                UserWalkDistancesDto.UserWalkDistanceDto.builder()
                                        .distance(rs.getInt("distanceMeter"))
                                        .date(rs.getDate("distanceDate").toLocalDate())
                                        .build())
                        .list()
        );
        return result;
    }

    /**
     * Create User Step tuple with id, distanceDate, distanceMeter. If existed, update.
     * @param uid user id
     * @param distanceDate target distanceDate
     * @param distanceMeter user's step count
     * @return True when query exactly play.
     */
    public boolean createUserWalkDistance(long uid, LocalDate distanceDate, int distanceMeter) {

        int affected =
                jdbcClient.sql(CREATE_DISTANCE_BY_ID_AND_DATE)
                        .param(uid)
                        .param(java.sql.Date.valueOf(distanceDate))
                        .param(distanceMeter)
                        .param(distanceMeter)
                        .update();

        return affected > 0;
    }

    public boolean deleteUserWalkDistance(long uid, LocalDate startDate, LocalDate endDate) {
        return jdbcClient.sql(DELETE_DISTANCE_BY_ID_AND_DURATION)
                .param(uid)
                .param(java.sql.Date.valueOf(startDate))
                .param(java.sql.Date.valueOf(endDate))
                .update() > 0;
    }

    public int getTotalDistance(long userid) {
        return jdbcClient.sql(GET_TOTAL_DISTANCE_BY_USER_ID)
            .param("userId", userid)
            .query(Integer.class)
            .optional()
            .orElse(0);
    }
}
