package com.dudoji.spring.models.dao;

import com.dudoji.spring.dto.user.UserSimpleDto;
import com.dudoji.spring.util.BuiltSql;
import com.dudoji.spring.util.SqlBuilder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository("FollowDao")
public class FollowDao {
    private static final Map<String, String> COL_MAP = Map.of(
        "name", "name",
        "email", "email",
        "followingAt", "followingAt",
        "followedAt", "followedAt"
    );

    private static final String GET_NONE_LIST_BY_ID_BASE = """
        SELECT
            id AS userId,
            name,
            email,
            profileImage,
            NULL AS followingAt,
            NULL AS followedAt
        FROM "User" u
        WHERE u.id != :userId  -- 나를 제외
          AND u.id NOT IN ( -- 팔로우 목록 제외
            -- 내가 팔로우하는 사람들의 ID 목록
            SELECT followeeId FROM follow WHERE followerId = :userId
            UNION
            -- 나를 팔로우하는 사람들의 ID 목록
            SELECT followerId FROM follow WHERE followeeId = :userId
        )
        """;

    private static final String GET_FOLLOWING_LIST_BY_ID = """ 
        SELECT u.id        AS userId,
                 u.name,
                 u.email,
                 u.profileImage,
                 f1.createdAt AS followingAt,   -- 내가 팔로잉한 시각
                 f2.createdAt AS followedAt     -- 상대가 날 팔로잉한 시각 (없으면 NULL)
          FROM "User" u
          JOIN follow f1
            ON u.id = f1.followeeId            -- 내가 팔로우하는 사람들
           AND f1.followerId = :userId
          LEFT JOIN follow f2
            ON f2.followerId = u.id            -- 상대가 나를 팔로우하는 경우
           AND f2.followeeId = :userId
          LIMIT :limit OFFSET :offset;
        """;

    private static final String GET_FOLLOWING_LIST_BY_ID_BASE = """
        SELECT u.id        AS userId,
                 u.name,
                 u.email,
                 u.profileImage,
                 f1.createdAt AS followingAt,   -- 내가 팔로잉한 시각
                 f2.createdAt AS followedAt     -- 상대가 날 팔로잉한 시각 (없으면 NULL)
          FROM "User" u
          JOIN follow f1
            ON u.id = f1.followeeId            -- 내가 팔로우하는 사람들
           AND f1.followerId = :userId
          LEFT JOIN follow f2
            ON f2.followerId = u.id            -- 상대가 나를 팔로우하는 경우
           AND f2.followeeId = :userId
        """;
    private static final String CREATE_FOLLOWING_BY_ID = "INSERT INTO follow (followerId, followeeId) VALUES (?, ?)";
    private static final String CREATE_FOLLOWING_WITH_SELECTING_DAY = "INSERT INTO follow (followerId, followeeId, createdAt) VALUES (?, ?, ?)";
    private static final String DELETE_FOLLOWING_BY_ID = "DELETE FROM follow WHERE followerId = ? AND followeeId = ?";
    private static final String IS_FOLLOWING = "SELECT 1 FROM follow WHERE followerId = ? AND followeeId = ?";

    private static final String GET_FOLLOWER_LIST_BY_ID_BASE = """
        SELECT u.id        AS userId,
                   u.name,
                   u.email,
                   u.profileImage,
                   f1.createdAt AS followedAt,    -- 그 사람이 나를 팔로우한 시각
                   f2.createdAt AS followingAt    -- 내가 그 사람을 팔로우한 시각 (없으면 NULL)
            FROM "User" u
            JOIN follow f1
              ON u.id = f1.followerId             -- 나를 팔로우하는 사람
             AND f1.followeeId = :userId
            LEFT JOIN follow f2
              ON f2.followerId = :userId          -- 내가 그 사람을 팔로우하는 경우
             AND f2.followeeId = u.id
        """;

    private static final String GET_FOLLOWER_LIST_BY_ID = """
        SELECT u.id        AS userId,
                   u.name,
                   u.email,
                   u.profileImage,
                   f1.createdAt AS followedAt,    -- 그 사람이 나를 팔로우한 시각
                   f2.createdAt AS followingAt    -- 내가 그 사람을 팔로우한 시각 (없으면 NULL)
            FROM "User" u
            JOIN follow f1
              ON u.id = f1.followerId             -- 나를 팔로우하는 사람
             AND f1.followeeId = :userId
            LEFT JOIN follow f2
              ON f2.followerId = :userId          -- 내가 그 사람을 팔로우하는 경우
             AND f2.followeeId = u.id
            LIMIT :limit OFFSET :offset;
        """;


    private static final String GET_NUM_OF_FOLLOWING = """
            SELECT count(1)
            FROM follow
            WHERE followerId = :userId;
            """;
    private static final String GET_NUM_OF_FOLLOWER = """
            SELECT count(1)
            FROM follow
            WHERE followeeId = :userId;
            """;

    private static final String GET_FOLLOWING_BULK_BY_ID = """
        SELECT "User".id as userId, name, email, profileImage
        FROM "User" JOIN follow ON "User".id = follow.followeeId
        WHERE followerId = :userId;
        """;
    /**
     * &#064;Deprecated
     */
    private static final String UPDATE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "UPDATE friend_request SET status = CAST(? AS friend_request_status) WHERE senderId = ? AND receiverId = ? AND status = CAST('PENDING' AS friend_request_status)";
    private static final String GET_FRIEND_REQUEST_BY_ID = "SELECT senderId FROM friend_request WHERE receiverId = ? AND status = CAST('PENDING' AS friend_request_status)";
    private static final String CREATE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "INSERT INTO friend_request (senderId, receiverId) VALUES (?, ?)";
    private static final String DELETE_FRIEND_REQUEST_BY_SENDER_RECEIVER = "DELETE friend_request WHERE senderId = ? AND receiverId = ? AND status = CAST('PENDING' AS friend_request_status)";

    private final RowMapper<UserSimpleDto> UserSimpleDtoMapper = (rs, rowNum) -> {
        return new UserSimpleDto(
            rs.getLong("userId"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("profileImage"),
            rs.getObject("followedAt", LocalDateTime.class),
            rs.getObject("followingAt", LocalDateTime.class)
        );
    };

    @Autowired
    private JdbcClient jdbcClient;

    public List<UserSimpleDto> getUsers (long userId, int offset, int limit, Sort sort, String keyword, String type) {
        String baseSql = switch (type) {
            case "NONE"      -> GET_NONE_LIST_BY_ID_BASE;
            case "FOLLOWING" -> GET_FOLLOWING_LIST_BY_ID_BASE;
            case "FOLLOWER"  -> GET_FOLLOWER_LIST_BY_ID_BASE;
            default          -> throw new IllegalArgumentException("Unknown type: " + type);
        };

        SqlBuilder sqlBuilder = new SqlBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            sqlBuilder
                .and("u.email ILIKE :keyword", "keyword", "%" + keyword + "%");
        }

        sqlBuilder.orderBy(sort, COL_MAP);

        BuiltSql builtSql = sqlBuilder.build();

        String sql = baseSql +
            builtSql.whereSql() +
            builtSql.orderBySql() +
            " LIMIT :limit OFFSET :offset";

        return jdbcClient.sql(sql)
            .param("userId", userId)
            .params(builtSql.params())
            .param("limit", limit)
            .param("offset", offset)
            .query(UserSimpleDtoMapper)
            .list();
    }

    // FOLLOWING
    /**
     * 백만 단위로 한 번에 받아오는 함수
     * @param userId user Id
     * @return
     */
    public List<UserSimpleDto> getFollowingListByUser(long userId) {
        return getFollowingListByUser(userId, 0, 1_000_000);
    }

    public List<UserSimpleDto> getFollowingListByUser(long userId, int offset, int limit) {
        return jdbcClient.sql(GET_FOLLOWING_LIST_BY_ID)
                .param("userId", userId)
                .param("limit", limit)
                .param("offset", offset)
                .query(UserSimpleDtoMapper)
                .list();
    }

    // FOLLOWER

    public List<UserSimpleDto> getFollowerListByUser(long userId) {
        return getFollowerListByUser(userId, 0, 1_000_000);
    }

    public List<UserSimpleDto> getFollowerListByUser(long userId, int offset, int limit) {
        return jdbcClient.sql(GET_FOLLOWER_LIST_BY_ID)
                .param("userId", userId)
                .param("limit", limit)
                .param("offset", offset)
                .query(UserSimpleDtoMapper)
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

    /**
     * 테스트를 위한 함수입니다. 날짜를 정해서 팔로잉 관계를 만듭니다.
     * @param userId 팔로우 하는 유저의 아이디
     * @param followeeId 팔로우 당하는 유저의 아이디
     * @param createdAt 만든 날짜
     * @return 진행 결과
     */
    public boolean createFollowingWithSelectingDay(long userId, long followeeId, LocalDate createdAt) {
        return jdbcClient.sql(CREATE_FOLLOWING_WITH_SELECTING_DAY)
            .param(userId)
            .param(followeeId)
            .param(createdAt)
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
