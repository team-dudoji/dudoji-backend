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

    @Deprecated
    @Autowired
    private DBConnection dbConnection;

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

    @Deprecated
    public User getUserByName(String name) {
        try (Connection connection  = dbConnection.getConnection();
             PreparedStatement preparedStatement =  connection.prepareStatement(GET_USER_BY_NAME);
        ) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()){
                    long uid = resultSet.getLong(1);
                    String email = resultSet.getString(2);
                    Timestamp createdAt = resultSet.getTimestamp(3);
                    String role = resultSet.getString(4);
                    String password = resultSet.getString(5);
                    return User.builder()
                            .id(uid)
                            .name(name)
                            .email(email)
                            .createAt(createdAt)
                            .role(role)
                            .password(password)
                            .build();
                }
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
//    @Deprecated
//    // 카카오 아이디를 잘 안 쓸 예정
//    // 수정 바람
//    public User getUserByKakaoId(long kakaoId) {
//        try (Connection connection = dbConnection.getConnection()) {
//            PreparedStatement preparedStatement =  connection.prepareStatement(GET_USER_BY_KAKAO_ID);
//            preparedStatement.setLong(1, kakaoId);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()){
//                long uid = resultSet.getLong(1);
//                String name = resultSet.getString(2);
//                String email = resultSet.getString(3);
//                Timestamp createdAt = resultSet.getTimestamp(4);
//                return User.builder()
//                        .id(uid)
//                        .name(name)
//                        .email(email)
//                        .createAt(createdAt)
//                        .build();
//            }
//            return null;
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

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
//        try (Connection connection  = dbConnection.getConnection();
//             PreparedStatement preparedStatement =  connection.prepareStatement(CREATE_USER_BY_USER);
//        ) {
//            preparedStatement.setString(1, user.getName());
//            preparedStatement.setString(2, user.getEmail());
//            preparedStatement.setString(3, user.getPassword());
//            preparedStatement.setString(4, user.getRole());
//            preparedStatement.setString(5, user.getProfileImageUrl());
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()){
//                    long uid = resultSet.getLong("id");
//                    return uid;
//                }
//            }
//            return -1;
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

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
//        List<User> friendList = new ArrayList<>();
//        try (Connection connection  = dbConnection.getConnection();
//             PreparedStatement preparedStatement =  connection.prepareStatement(GET_USERS_BY_EMAIL_LIKE);
//        ) {
//            preparedStatement.setString(1, email);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                while (resultSet.next()){
//                    if (count <= 0) break;
//                    long uid = resultSet.getLong(1);
//                    String name = resultSet.getString(2);
//                    Timestamp createdAt = resultSet.getTimestamp(3);
//                    String role = resultSet.getString(4);
//                    String emailTrue = resultSet.getString(5);
//                    friendList.add(User.builder()
//                            .id(uid)
//                            .name(name)
//                            .email(email)
//                            .createAt(createdAt)
//                            .role(role)
//                            .email(emailTrue)
//                            .build());
//                    count--; // TODO; 임시로 5개로 제한합니다. 필요시 count 변수 수정
//                }
//            }
//            return friendList;
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }
}
