package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository("FollowDao")
public class FollowDao {

    private static String GET_FOLLOW_LIST_BY_ID = "SELECT (followee_id) FROM follow WHERE follower_id = ?";
    private static String CREATE_FOLLOW_BY_ID = "INSERT INTO follow (follower_id, followee_id) VALUES (?, ?)";
    private static String DELETE_FOLLOW_BY_ID = "DELETE FROM follow WHERE follower_id = ? AND followee_id = ?";
    private static String IS_FOLLOW_EXIST = "SELECT 1 FROM follow WHERE follower_id = ? AND followee_id = ?";

    /**
     * &#064;Deprecated
     */
    private static String UPDATE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "UPDATE friend_request SET status = CAST(? AS friend_request_status) WHERE sender_id = ? AND receiver_id = ? AND status = CAST('PENDING' AS friend_request_status)";
    private static String GET_FRIEND_REQUEST_BY_ID = "SELECT sender_id FROM friend_request WHERE receiver_id = ? AND status = CAST('PENDING' AS friend_request_status)";
    private static String CREATE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "INSERT INTO friend_request (sender_id, receiver_id) VALUES (?, ?)";
    private static String DELETE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "DELETE friend_request WHERE sender_id = ? AND receiver_id = ? AND status = CAST('PENDING' AS friend_request_status)";

    @Autowired
    private JdbcClient jdbcClient;

    // TODO: 이름 명확하게 할 것
    public List<Long> getFollowerListByUser(long userId) {
        return jdbcClient.sql(GET_FOLLOW_LIST_BY_ID)
                .param(userId)
                .query(Long.class)
                .list();
    }

    public boolean isFollowing(long userId, long followeeId) {
        return jdbcClient.sql(IS_FOLLOW_EXIST) // TODO: TEST NEEDED
                .param(userId)
                .param(followeeId)
                .query()
                .optionalValue().isPresent();
    }

    public boolean createFollowByUser(long userId, long followeeId) {
        return jdbcClient.sql(CREATE_FOLLOW_BY_ID)
                .param(userId)
                .param(followeeId)
                .update() > 0;
    }

    public boolean deleteFollowByUser(long userId, long followeeId) {
        return jdbcClient.sql(DELETE_FOLLOW_BY_ID)
                .param(userId)
                .param(followeeId)
                .update() > 0;
    }

    /**
        &#064;Deprecated
        Below Methods are deprecated.
        If we use the secret account, can be reactivated and used again in the future.
        25/06/27 Not Update to jdbcClient
     */

    /*
    @Deprecated
    // If We Use Secret Account. Using it
    public List<Long> getFollowRequestList(long userId) {
        try (Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_FRIEND_REQUEST_BY_ID);
        ) {
            List<Long> friendRequestList = new ArrayList<>();
            preparedStatement.setLong(1, userId);

            try (ResultSet resultSet =  preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    friendRequestList.add(resultSet.getLong(1));
                }
                return friendRequestList;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public boolean acceptFollow(long senderId, long receiverId) {
        try (Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_FRIEND_REQUEST_BY_SENDER_RECEIVER);
        ) {
            preparedStatement.setString(1, "ACCEPTED");
            preparedStatement.setLong(2, senderId);
            preparedStatement.setLong(3, receiverId);
            int updated = preparedStatement.executeUpdate();
            return updated > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public boolean rejectFollow(long senderId, long receiverId) {
        try (Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_FRIEND_REQUEST_BY_SENDER_RECEIVER);
        ) {
            preparedStatement.setString(1, "REJECTED");
            preparedStatement.setLong(2, senderId);
            preparedStatement.setLong(3, receiverId);
            int updated = preparedStatement.executeUpdate();
            return updated > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public boolean requestFollow(long senderId, long receiverId) {
        try (Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatementDelete = connection.prepareStatement(DELETE_FRIEND_REQUEST_BY_SENDER_RECEIVER);
        ) {
            preparedStatementDelete.setLong(1, senderId);
            preparedStatementDelete.setLong(2, receiverId);
            int updated = preparedStatementDelete.executeUpdate();
            return updated > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    */
}
