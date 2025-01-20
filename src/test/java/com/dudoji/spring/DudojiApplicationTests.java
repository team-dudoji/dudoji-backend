package com.dudoji.spring;

import com.dudoji.spring.models.DBConnection;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest
class DudojiApplicationTests {


    @Test
    void testDBConnection() {
        try{
            DBConnection.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}
