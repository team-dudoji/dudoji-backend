package com.dudoji.spring;

import com.dudoji.spring.models.DBConnection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DudojiApplicationTests {

    @Autowired
    DBConnection dbConnection;

    @Test
    void testDBConnection() {

        try (Connection connection = dbConnection.getConnection()) {
            assertNotNull(connection, "Database Connection should not be null");
            System.out.println("Database connection successful");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("testDBConnection: Fail Tast");
        }
    }

}
