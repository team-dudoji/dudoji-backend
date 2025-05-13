package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.User;
import com.dudoji.spring.service.FriendService;
import com.dudoji.spring.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FriendDao {

    // TODO: DB 결정 시 작성
    private static String GET_FRIEND_LIST_BY_SMALLID = "SELECT (big_id) FROM friends WHERE small_id = ?";
    private static String GET_FRIEND_LIST_BY_BIGID = "SELECT (small_id) FROM friends WHERE big_id = ?";
    private static String CREATE_FRIEND_BY_ID = "INSERT INTO friends (small_id, big_id) VALUES (?, ?)";
    private static String DELETE_FRIEND_BY_ID = "DELETE FROM friends (small_id, big_id) WHERE small_id = ? AND big_id = ?";
    private static String UPDATE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "UPDATE friend_request SET status = CAST(? AS friend_request_status) WHERE sender_id = ? AND receiver_id = ? AND status = CAST('PENDING' AS friend_request_status)";
    private static String GET_FRIEND_REQUEST_BY_ID = "SELECT sender_id FROM friend_request WHERE receiver_id = ? AND status = CAST('PENDING' AS friend_request_status)";
    private static String CREATE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "INSERT INTO friend_request (sender_id, receiver_id) VALUES (?, ?)";

    @Autowired
    private DBConnection dbConnection;

    public List<Long> getFriendListByUser(long userId) {
        List<Long> friendList = new ArrayList<>();
        List<String> queryList = new ArrayList<>();
        queryList.add(GET_FRIEND_LIST_BY_SMALLID);
        queryList.add(GET_FRIEND_LIST_BY_BIGID);
        try (Connection connection = dbConnection.getConnection()) {
            for (String query : queryList) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    friendList.add(resultSet.getLong(1));
                }
            }
            return friendList;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean createFriendByUser(long userId, long friendId) {
        long smallId = Math.min(userId, friendId);
        long bigId = Math.max(userId, friendId);

        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_FRIEND_BY_ID);
            preparedStatement.setLong(1, smallId);
            preparedStatement.setLong(2, bigId);
            preparedStatement.execute();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteFriendByUser(long userId, long friendId) {
        long smallId = Math.min(userId, friendId);
        long bigId = Math.max(userId, friendId);

        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FRIEND_BY_ID);
            preparedStatement.setLong(1, smallId);
            preparedStatement.setLong(2, bigId);
            preparedStatement.execute();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Long> getFriendRequestList (long userId) {
        try (Connection connection = dbConnection.getConnection()) {
            List<Long> friendRequestList = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_FRIEND_REQUEST_BY_ID);
            preparedStatement.setLong(1, userId);
            ResultSet resultSet =  preparedStatement.executeQuery();
            while (resultSet.next()) {
                friendRequestList.add(resultSet.getLong(1));
            }
            return friendRequestList;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean acceptFriend(long senderId, long receiverId) {
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_FRIEND_REQUEST_BY_SENDER_RECEIVER);
            preparedStatement.setString(1, "ACCEPTED");
            preparedStatement.setLong(2, senderId);
            preparedStatement.setLong(3, receiverId);
            int updated = preparedStatement.executeUpdate();
            return updated > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean rejectFriend(long senderId, long receiverId) {
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_FRIEND_REQUEST_BY_SENDER_RECEIVER);
            preparedStatement.setString(1, "REJECTED");
            preparedStatement.setLong(2, senderId);
            preparedStatement.setLong(3, receiverId);
            int updated = preparedStatement.executeUpdate();
            return updated > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean requestFriend(long senderId, long receiverId) {
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_FRIEND_REQUEST_BY_SENDER_RECEIVER);
            preparedStatement.setLong(1, senderId);
            preparedStatement.setLong(2, receiverId);
            int updated = preparedStatement.executeUpdate();
            return updated > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
