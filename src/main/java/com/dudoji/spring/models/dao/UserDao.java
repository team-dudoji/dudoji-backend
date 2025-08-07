package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("UserDao")
public class UserDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static final String GET_USER_BY_ID =
            "SELECT id, name, email, password, role, provider, providerId, createdAt, profileImage, coin "
                + "from \"User\" where id=?";
    private static final String GET_USER_BY_NAME =
            "SELECT id, name, email, password, role, provider, providerId, createdAt, profileImage, coin "
                + "FROM \"User\" WHERE name=?";
    private static final String GET_USER_BY_EMAIL =
            "SELECT id, name, email, password, role, provider, providerId, createdAt, profileImage, coin "
                + "FROM \"User\" WHERE email=?";
    private static final String REMOVE_USER_BY_ID =
            "delete from \"User\" where id=?";
    private static final String CREATE_USER_BY_ID =
            "insert into \"User\"(name, email) values (?, ?) returning id";
    private static final String CREATE_USER_BY_USER =
            "insert into \"User\"(name, email, password, role, profileImage) values (?, ?, ?, ?::user_role, ?) returning id";
    private static final String GET_USER_BY_KAKAO_ID =
            "SELECT id, name, email, createdAt FROM \"User\" WHERE kakao_id=?";
    private static final String CREATE_USER_BY_KAKAO_ID =
            "insert into \"User\"(name, email, kakao_id) values (?, ?, ?) returning id";
    private static final String GET_PROFILE_IMAGE_BY_ID =
            "SELECT profileImage FROM \"User\" WHERE id=?";
    private static final String GET_USERS_BY_EMAIL_LIKE =
        "SELECT id, name, email, password, role, provider, providerId, createdAt, profileImage, coin " +
            "FROM \"User\" WHERE email LIKE '%' || ? || '%'";
    private static final String UPDATE_COIN = """
            UPDATE \"User\" 
            SET coin = :coin
            WHERE id = :userId;
            """;


    private static final RowMapper<User> UserMapper = (rs, rowNum) -> new User(
        rs.getLong("id"),
        rs.getString("password"),
        rs.getString("role"),
        rs.getString("name"),
        rs.getString("email"),
        rs.getDate("createdAt"),
        rs.getString("provider"),
        rs.getString("providerId"),
        rs.getString("profileImage"),
        rs.getInt("coin")
    );

    public void setUpdateCoin(long uid, int newCoin) {
        jdbcClient.sql(UPDATE_COIN)
                .param("userId", uid)
                .param("coin", newCoin)
                .update();
    }

    public User getUserById(long uid) {
        return jdbcClient.sql(GET_USER_BY_ID)
                .param(uid)
                .query(UserMapper)
                .optional()
                .orElse(null);
    }

    public User getUserByName(String name) {
        return jdbcClient.sql(GET_USER_BY_NAME)      // "SELECT id, email, createdAt, role, password FROM \"User\" WHERE name=?"
                .param(name)
                .query(UserMapper)
                .optional()
                .orElse(null);
    }

    public User getUserByEmail(String email) {
        return jdbcClient.sql(GET_USER_BY_EMAIL)
                .param(email)
                .query(UserMapper)
                .optional()
                .orElse(null);
    }

    public boolean removeUserById(long uid) {
        return jdbcClient.sql(REMOVE_USER_BY_ID)
                .param(uid)
                .update() == 1;
    }

    public long createUserByUser(User user) {

        return jdbcClient.sql(CREATE_USER_BY_USER)
                .param(user.getName())
                .param(user.getEmail())
                .param(user.getPassword())
                .param(user.getRole())
                .param(user.getProfileImageUrl())
                .query(Long.class)
                .single();
    }

    public String getProfileImageUrl(long uid) {
        return jdbcClient.sql(GET_PROFILE_IMAGE_BY_ID)
                .param(uid)
                .query(String.class)
                .optional()
                .orElse(null);
    }

    public List<User> getRecommendedUsers(String email) {
        int count = 5;
        return jdbcClient.sql(GET_USERS_BY_EMAIL_LIKE)
                .param(email)
                .query(UserMapper)
                .list()
                .stream()
                .limit(count)
                .toList();
    }
}
