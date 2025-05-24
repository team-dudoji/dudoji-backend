package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class LikesDao {

    private static String LIKE_PIN_BY_ID = "INSERT INTO likes (user_id, pin_id) VALUES (?, ?)";
    private static String UNLIKE_PIN_BY_ID = "DELETE FROM likes WHERE user_id = ? AND pin_id = ?";
    private static String GET_LIKE_COUNT_BY_ID = "SELECT like_count FROM like_counts WHERE pin_id = ?";
    private static String GET_IS_LIKED_BY_ID = "SELECT EXISTS (\n" +
            "    SELECT 1 FROM likes WHERE pin_id = ? AND user_id = ?\n" +
            ")";
    private static String REFRESH_LIKES = "REFRESH MATERIALIZED VIEW like_counts";

    @Autowired
    private DBConnection dbConnection;

    public boolean likePin(long userId, long pinId) {
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(LIKE_PIN_BY_ID);
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, pinId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean unlikePin(long userId, long pinId) {
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UNLIKE_PIN_BY_ID);
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, pinId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int getLikesCount(long pinId) {
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_LIKE_COUNT_BY_ID);
            preparedStatement.setLong(1, pinId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            else return 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLiked(long userId, long pinId) {
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_IS_LIKED_BY_ID);
            preparedStatement.setLong(1, pinId);
            preparedStatement.setLong(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public void refreshViews() {
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(REFRESH_LIKES);
            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
