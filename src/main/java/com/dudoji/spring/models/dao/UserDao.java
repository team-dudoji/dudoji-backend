package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.KakaoUser;
import com.dudoji.spring.models.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository("UserDao")
public class UserDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static String GET_USER_BY_ID =
            "select name, email, created_at, role, profile_image from \"User\" where id=?";
    private static String GET_USER_BY_NAME =
            "SELECT id, email, created_at, role, password FROM \"User\" WHERE name=?";
    private static String GET_USER_BY_EMAIL =
            "SELECT id, name, created_at, role, password FROM \"User\" WHERE email=?";
    private static String REMOVE_USER_BY_ID =
            "delete from \"User\" where id=?";
    private static String CREATE_USER_BY_ID =
            "insert into \"User\"(name, email) values (?, ?) returning id";
    private static String CREATE_USER_BY_USER =
            "insert into \"User\"(name, email, password, role, profile_image) values (?, ?, ?, ?::user_role, ?) returning id";
    private static String GET_USER_BY_KAKAO_ID =
            "SELECT id, name, email, created_at FROM \"User\" WHERE kakao_id=?";
    private static String CREATE_USER_BY_KAKAO_ID =
            "insert into \"User\"(name, email, kakao_id) values (?, ?, ?) returning id";
    private static String GET_PROFILE_IMAGE_BY_ID =
            "SELECT profile_image FROM \"User\" WHERE id=?";
    private static String GET_USERS_BY_EMAIL_LIKE =
            "SELECT id, name, created_at, role, email FROM \"User\" WHERE email LIKE '%' || ? || '%'";


    public User getUserById(long uid) {
        return jdbcClient.sql(GET_USER_BY_ID)
                .param(uid)
                .query( (rs, rowNum) ->
                        User.builder()
                                .id(uid)
                                .name(rs.getString(1))
                                .email(rs.getString(2))
                                .createAt(rs.getTimestamp(3))
                                .role(rs.getString(4))
                                .profileImageUrl(rs.getString(5))
                                .build())
                .optional()
                .orElse(null);
    }

    public User getUserByName(String name) {
        return jdbcClient.sql(GET_USER_BY_NAME)      // "SELECT id, email, created_at, role, password FROM \"User\" WHERE name=?"
                .param(name)
                .query((rs, rowNum) ->
                        User.builder()
                                .id(rs.getLong(1))
                                .name(name)
                                .email(rs.getString(2))
                                .createAt(rs.getTimestamp(3))
                                .role(rs.getString(4))
                                .password(rs.getString(5))
                                .build())
                .optional()
                .orElse(null);
    }

    public User getUserByEmail(String email) {
        return jdbcClient.sql(GET_USER_BY_EMAIL)
                .param(email)
                .query((rs, rowNum) ->
                        User.builder()
                                .id(rs.getLong(1))
                                .name(rs.getString(2))
                                .email(email)
                                .createAt(rs.getTimestamp(3))
                                .role(rs.getString(4))
                                .password(rs.getString(5))
                                .build())
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
                .query((rs, rowNum) ->
                        User.builder()
                                .id(rs.getLong(1))
                                .name(rs.getString(2))
                                .createAt(rs.getTimestamp(3))
                                .role(rs.getString(4))
                                .email(rs.getString(5))
                                .build())
                .list()
                .stream()
                .limit(count)
                .toList();
    }
}
