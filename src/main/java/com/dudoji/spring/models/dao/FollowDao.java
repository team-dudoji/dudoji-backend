package com.dudoji.spring.models.dao;

import com.dudoji.spring.dto.user.UserSimpleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository("FollowDao")
public class FollowDao {

    private static String GET_FOLLOWING_LIST_BY_ID = """
          SELECT "User".id as userId, name, email, profile_image as profileImage
          FROM "User" JOIN follow ON "User".id = follow.followee_id
          WHERE follower_id = :userId;
          """;
    private static String CREATE_FOLLOWING_BY_ID = "INSERT INTO follow (follower_id, followee_id) VALUES (?, ?)";
    private static String DELETE_FOLLOWING_BY_ID = "DELETE FROM follow WHERE follower_id = ? AND followee_id = ?";
    private static String IS_FOLLOWING = "SELECT 1 FROM follow WHERE follower_id = ? AND followee_id = ?";

    private static String GET_FOLLOWER_LIST_BY_ID = """
            SELECT "User".id as userId, name, email, profile_image as profileImage
            FROM "User" JOIN follow ON "User".id = follow.follower_id
            WHERE followee_id = :userId;
            """;

    private static String GET_NUM_OF_FOLLOWING = """
            SELECT count(1)
            FROM follow
            WHERE follower_id = :userId;
            """;
    private static String GET_NUM_OF_FOLLOWER = """
            SELECT count(1)
            FROM follow
            WHERE followee_id = :userId;
            """;

    /**
     * &#064;Deprecated
     */
    private static String UPDATE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "UPDATE friend_request SET status = CAST(? AS friend_request_status) WHERE sender_id = ? AND receiver_id = ? AND status = CAST('PENDING' AS friend_request_status)";
    private static String GET_FRIEND_REQUEST_BY_ID = "SELECT sender_id FROM friend_request WHERE receiver_id = ? AND status = CAST('PENDING' AS friend_request_status)";
    private static String CREATE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "INSERT INTO friend_request (sender_id, receiver_id) VALUES (?, ?)";
    private static String DELETE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "DELETE friend_request WHERE sender_id = ? AND receiver_id = ? AND status = CAST('PENDING' AS friend_request_status)";

    @Autowired
    private JdbcClient jdbcClient;

    public List<UserSimpleDto> getFollowingListByUser(long userId) {
        return jdbcClient.sql(GET_FOLLOWING_LIST_BY_ID)
                .param("userId", userId)
                .query((rs, numOfRows) -> new UserSimpleDto(
                        rs.getLong("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("profileImage")
                ))
                .list();
    }

    public List<UserSimpleDto> getFollowerListByUser(long userId) {
        return jdbcClient.sql(GET_FOLLOWER_LIST_BY_ID)
                .param("userId", userId)
                .query((rs, numOfRows) -> new UserSimpleDto(
                        rs.getLong("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("profileImage")
                ))
                .list();
    }

    public boolean isFollowing(long userId, long followeeId) {
        return jdbcClient.sql(IS_FOLLOWING) // TODO: TEST NEEDED
                .param(userId)
                .param(followeeId)
                .query()
                .optionalValue().isPresent();
    }

    public boolean createFollowingByUser(long userId, long followeeId) {
        return jdbcClient.sql(CREATE_FOLLOWING_BY_ID)
                .param(userId)
                .param(followeeId)
                .update() > 0;
    }

    public boolean deleteFollowingByUser(long userId, long followeeId) {
        return jdbcClient.sql(DELETE_FOLLOWING_BY_ID)
                .param(userId)
                .param(followeeId)
                .update() > 0;
    }

    public int getNumOfFollowerByUserId(long userId) {
        Long num = (Long) jdbcClient.sql(GET_NUM_OF_FOLLOWER)
                .param("userId", userId)
                .query()
                .singleValue();
        return num.intValue();
    }

    public int getNumOfFollowingByUserId(long userId) {
        Long num = (Long) jdbcClient.sql(GET_NUM_OF_FOLLOWING)
                .param("userId", userId)
                .query()
                .singleValue();
        return num.intValue();
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
