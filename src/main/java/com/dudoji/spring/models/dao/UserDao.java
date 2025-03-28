package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.KakaoUser;
import com.dudoji.spring.models.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class UserDao {

    @Autowired
    private DBConnection dbConnection;

    private static String GET_USER_BY_ID =
            "select name, email, created_at, role from \"User\" where id=?";
    private static String GET_USER_BY_NAME =
            "SELECT id, email, created_at, role, password FROM \"User\" WHERE name=?";
    private static String REMOVE_USER_BY_ID =
            "delete from \"User\" where id=?";
    private static String CREATE_USER_BY_ID =
            "insert into \"User\"(name, email) values (?, ?) returning id";
    private static String CREATE_USER_BY_USER =
            "insert into \"User\"(name, email, password, role) values (?, ?, ?, ?::user_role) returning id";
    private static String GET_USER_BY_KAKAO_ID =
            "SELECT id, name, email, created_at FROM \"User\" WHERE kakao_id=?";
    private static String CREATE_USER_BY_KAKAO_ID =
            "insert into \"User\"(name, email, kakao_id) values (?, ?, ?) returning id";

    public User getUserById(long uid) {
        try (Connection connection  = dbConnection.getConnection()) {
            PreparedStatement preparedStatement =  connection.prepareStatement(GET_USER_BY_ID);
            preparedStatement.setLong(1, uid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String name = resultSet.getString(1);
                String email = resultSet.getString(2);
                Timestamp createdAt = resultSet.getTimestamp(3);
                String role = resultSet.getString(4);
                return User.builder()
                        .id(uid)
                        .name(name)
                        .email(email)
                        .createAt(createdAt)
                        .role(role).build();
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUserByName(String name) {
        try (Connection connection  = dbConnection.getConnection()) {
            PreparedStatement preparedStatement =  connection.prepareStatement(GET_USER_BY_NAME);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
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
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        try (Connection connection  = dbConnection.getConnection()) {
            PreparedStatement preparedStatement =  connection.prepareStatement(REMOVE_USER_BY_ID);
            preparedStatement.setLong(1, uid);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    // User로 만들도록 하자. 두 개의 정보만으로는 데베 저장이 버겁다.
    public long createUser(String name, String email) {
        try (Connection connection  = dbConnection.getConnection()) {
            PreparedStatement preparedStatement =  connection.prepareStatement(CREATE_USER_BY_ID);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                long uid = resultSet.getLong("id");
                return uid;
            }
            return -1;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public long createUserByUser(User user) {
        try (Connection connection  = dbConnection.getConnection()) {
            PreparedStatement preparedStatement =  connection.prepareStatement(CREATE_USER_BY_USER);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getRole());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                long uid = resultSet.getLong("id");
                return uid;
            }
            return -1;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Deprecated
    // 카카오 아이디를 안쓸 듯

    public long createUserWithKakaoId(String name, String email, long kakaoId) {
        try (Connection connection  = dbConnection.getConnection()) {
            PreparedStatement preparedStatement =  connection.prepareStatement(CREATE_USER_BY_KAKAO_ID);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setLong(3, kakaoId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                long uid = resultSet.getLong("id");
                return uid;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
}
