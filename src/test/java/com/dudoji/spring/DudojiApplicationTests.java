package com.dudoji.spring;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Pair;
import com.dudoji.spring.models.domain.User;
import com.dudoji.spring.models.domain.Point;
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

    @Test
    void pointClassTest() {
        Pair<Double, Double> googleMercator = Point.convertLatLngToGoogleMercator(41.85, -87.65);
        // 기대값
        double expectedX = 65.67111111111112;
        double expectedY = 95.17492654697409;
        // double 비교 시 오차범위(예: 1e-7) 내로 비교
        assertEquals(expectedX, googleMercator.getX(), 1e-7, "googleX가 예상 범위와 다릅니다.");
        assertEquals(expectedY, googleMercator.getY(), 1e-7, "googleY가 예상 범위와 다릅니다.");

        Pair<Integer, Integer> pixelPoint = Point.convertGoogleMercatorToPixel(googleMercator.getX(), googleMercator.getY());

        int expectedPixelX = 2151910;
        int expectedPixelY = 3118691;
        assertEquals(expectedPixelX, pixelPoint.getX());
        assertEquals(expectedPixelY, pixelPoint.getY());

        Pair<Integer, Integer> tilePoint = Point.convertGoogleMercatorToTile(googleMercator.getX(), googleMercator.getY());

        int expectedTileX = 8405;
        int expectedTileY = 12182;
        assertEquals(expectedTileX, tilePoint.getX());
        assertEquals(expectedTileY, tilePoint.getY());
    }
}
