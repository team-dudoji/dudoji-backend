package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class UserDao {

    @Autowired
    private DBConnection dbConnection;

    private static String GET_USER_BY_ID =
            "select name, email, created_at from \"User\" where id=?";
    private static String REMOVE_USER_BY_ID =
            "delete from \"User\" where id=?";
    private static String CREATE_USER_BY_ID =
            "insert into \"User\"(name, email) values (?, ?) returning id";

    public User getUserById(long uid) {
        try (Connection connection  = dbConnection.getConnection()) {
            PreparedStatement preparedStatement =  connection.prepareStatement(GET_USER_BY_ID);
            preparedStatement.setLong(1, uid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String name = resultSet.getString(1);
                String email = resultSet.getString(2);
                Timestamp createdAt = resultSet.getTimestamp(3);
                return new User(uid, name, email, createdAt);
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean removeUserById(long uid) {
        try (Connection connection  = dbConnection.getConnection()) {
            PreparedStatement preparedStatement =  connection.prepareStatement(REMOVE_USER_BY_ID);
            preparedStatement.setLong(1, uid);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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
}
