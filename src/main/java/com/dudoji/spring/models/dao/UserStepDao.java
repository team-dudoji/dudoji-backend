package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.UserStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
public class UserStepDao {

    @Autowired
    private DBConnection dbConnection;

    // SQL Query String
    private static String GET_STEP_BY_ID_AND_DATE = "SELECT * FROM user_steps WHERE user_id=? AND step_date=?";

    private static String GET_STEP_BY_ID_AND_DURATION = "SELECT * FROM user_steps WHERE user_id=? AND step_date BETWEEN ? AND ? ORDER BY step_date";

    private static String CREATE_STEP_BY_ID_AND_DATE = "INSERT INTO user_steps (user_id, step_date, step_count) VALUES (?, ?, ?)" +
            "ON CONFLICT (user_id, step_date) DO UPDATE SET step_count=?";



    /**
     * Get User Step Info by user id and date
     * @param uid user id
     * @param date date which we want to know step count
     * @return UserStep Object Created with uid, date
     */
    public UserStep getUserStepByIdOnDate(long uid, LocalDate date) {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_STEP_BY_ID_AND_DATE);
        ) {

            preparedStatement.setLong(1, uid);
            preparedStatement.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long stepId = resultSet.getLong("id");
                    int step = resultSet.getInt("step_count");
                    return UserStep.builder().uid(uid).stepId(stepId).stepMeter(step).stepDate(date).build();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<UserStep> getUserStepByIdOnDuration(long uid, LocalDate startDate, LocalDate endDate) {
        List<UserStep> result = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_STEP_BY_ID_AND_DURATION);
        ) {

            preparedStatement.setLong(1, uid);
            preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(3, java.sql.Date.valueOf(endDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long stepId = resultSet.getLong("id");
                    int step = resultSet.getInt("step_count");
                    LocalDate stepDate = resultSet.getDate("step_date").toLocalDate();
                    result.add(UserStep.builder().uid(uid).stepId(stepId).stepMeter(step).stepDate(stepDate).build());
                }
            }

            return result;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create User Step tuple with id, date, stepCount. If existed, update.
     * @param uid user id
     * @param date target date
     * @param stepCount user's step count
     * @return True when query exactly play.
     */
    public boolean createUserStep(long uid, LocalDate date, int stepCount) {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_STEP_BY_ID_AND_DATE);
        ) {

            preparedStatement.setLong(1, uid);
            preparedStatement.setDate(2, java.sql.Date.valueOf(date));
            preparedStatement.setInt(3, stepCount);
            preparedStatement.setInt(4, stepCount);
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
