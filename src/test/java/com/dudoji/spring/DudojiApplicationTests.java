package com.dudoji.spring;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testMapSectionCreating() {
        MapSection mapSection = new MapSection.Builder()
                .setUid(1)
                .setX(0)
                .setY(0)
                .build();
        assertNotNull(mapSection, "MapSection created Successful");
        MapSection detailMapSection = new MapSection.Builder()
                .setUid(1)
                .setX(0)
                .setY(0)
                .setBitmap(new byte[] {1, 2, 3})
                .build();
        assertNotNull(detailMapSection, "DetailMapSection created Successful");
    }

    @Autowired
    UserDao userDao;
    @Test
    void testUserDao(){
        String name = "Demo";
        String email = "demo@demo.com";
        long uid = userDao.createUser(name, email);
        assertNotEquals(uid, -1);


        User user = userDao.getUserById(uid);

        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());

        assertTrue(userDao.removeUserById(uid));

        System.out.println("User Dao is Successfully worked");
    }
}
