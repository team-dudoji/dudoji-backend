package com.dudoji.spring;

import com.dudoji.spring.dto.UserWalkDistancesDto;
import com.dudoji.spring.models.dao.UserWalkDistanceDao;
import com.dudoji.spring.models.domain.*;
import com.dudoji.spring.service.MapSectionService;
import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.dao.MapSectionDao;
import com.dudoji.spring.models.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.dudoji.spring.models.domain.MapSection.BASIC_ZOOM_SIZE;
import static com.dudoji.spring.models.domain.MapSection.TILE_SIZE;
import static com.dudoji.spring.util.BitmapUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DudojiApplicationTests {

    @Autowired
    DBConnection dbConnection;
    @Autowired
    MapSectionDao mapSectionDao;
    @Autowired
    MapSectionService mapSectionService;
    @Autowired
    UserWalkDistanceDao userstepDao;
    @Autowired
    private UserWalkDistanceDao userWalkDistanceDao;

    private final int uid = 2; // LeeYoWhan

    @Test
    void testUserWalkDistanceDao() {

        int meter = 5000;
        LocalDate date = new Date(System.currentTimeMillis()).toLocalDate();

        boolean checkCreate = userstepDao.createUserWalkDistance(uid, date, meter);
        assertTrue(checkCreate);

        UserWalkDistancesDto userWalkDistance = userWalkDistanceDao.getUserWalkDistanceByIdOnDuration(uid, date, date);
        assertNotNull(userWalkDistance);
        assertEquals(meter, userWalkDistance.getUserWalkDistances().getFirst().getDistance());
        assertEquals(date, userWalkDistance.getUserWalkDistances().getFirst().getDate());

        meter += 3000;
        checkCreate = userstepDao.createUserWalkDistance(uid, date, meter);
        assertTrue(checkCreate);

        userWalkDistance = userWalkDistanceDao.getUserWalkDistanceByIdOnDuration(uid, date, date);
        assertNotNull(userWalkDistance);
        assertEquals(meter, userWalkDistance.getUserWalkDistances().getFirst().getDistance());
        assertEquals(date, userWalkDistance.getUserWalkDistances().getFirst().getDate());

        boolean checkDelete = userstepDao.deleteUserWalkDistance(uid, date, date);
        assertTrue(checkDelete);
    }

    @Deprecated
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

    @Test
    void testMapSectionDao() {
        // 실행시 잘 작동되고 변경이 잘 됨.

        Point centerPoint = Point.fromGeographic(127.189969,37.469509);
        mapSectionService.applyRevealCircle(1, centerPoint, 1000);
    }

    @Autowired
    UserDao userDao;
    @Test
    void testUserDao(){
//        String name = "Demo";
//        String email = "demo@demo.com";
//        long kakao_id_sample = 1;
//        long uid = userDao.createUser(name, email);
//        assertNotEquals(uid, -1);
//
//        User user = userDao.getUserById(uid);
//
//        assertEquals(name, user.getName());
//        assertEquals(email, user.getEmail());
//
//        assertTrue(userDao.removeUserById(uid));
//
//        KakaoUser user2 = (KakaoUser) userDao.getUserByKakaoId(kakao_id_sample);
//
//        assertEquals(kakao_id_sample, user2.getKakaoId());
//        assertEquals("dudoji", user2.getName());
//
//        System.out.println("User Dao is Successfully worked");
    }

    @Test
    void testPointClass() {

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

    @Test
    void testBitmapUtil() {
        // Create BitMap
        byte[] bitmap = new byte[256 * 256 / 8];
        // Create Test Point
        Point testPoint = Point.fromGeographic(41.85, -87.65);

        // setBit Test
        boolean isSet = getBit(bitmap, testPoint);
        assertFalse(isSet);
        setBit(bitmap, testPoint);
        isSet = getBit(bitmap, testPoint);
        assertTrue(isSet);

        // setCloseBits Test
        Point centerPoint = Point.fromGeographic(37, 25);
        double radiusMeters = 30.0;
        setCloseBits(bitmap, centerPoint, radiusMeters, BASIC_ZOOM_SIZE);
        {
            Pair<Double, Double> centerGoogle = centerPoint.getGoogleMap();
            Pair<Integer, Integer> cpx = Point.convertGoogleMercatorToPixel(centerGoogle.getX(), centerGoogle.getY());

            int cx = cpx.getX() % TILE_SIZE; // center Point 비트맵 값
            int cy = cpx.getY() % TILE_SIZE; // center Point 비트맵 값

            boolean centerSet = getBit(bitmap, centerPoint);
            assertTrue(centerSet);

            int testX = cx + 3;
            int testY = cy + 1;

            if (testX < 0 || testX >= TILE_SIZE || testY < 0 || testY >= TILE_SIZE) {
                // 범위 체크
                System.out.println("Test point out of tile range!");
            }
            else {
                boolean nearSet = getBit(bitmap, testX, testY);
                assertTrue(nearSet);
            }

            // 3번째 실험
            int farX = cx + 100;
            int farY = cy + 1;
            if (farX < 0 || farX >= TILE_SIZE || farY < 0 || farY >= TILE_SIZE) {
                System.out.println("Far Point out of tile range");
            }
            else {
                boolean farSet = getBit(bitmap, farX, farY);
                assertFalse(farSet);
            }
        }

        // isExplored Test
        byte[] isExploredbitmap = new byte[TILE_SIZE * TILE_SIZE / 8];

        // Test When All Zero
        assertFalse(isExplored(isExploredbitmap));

        // Test When 80% fill
        fillBits(isExploredbitmap, (TILE_SIZE * TILE_SIZE) * 4 / 5 + 1); // 앞쪽 비트 절반만 켠다
        assertTrue(isExplored(isExploredbitmap));

        // Test When 100% fill
        fillBits(isExploredbitmap, TILE_SIZE * TILE_SIZE); // 전체 65536bit 켜기
        assertTrue(isExplored(isExploredbitmap));

    }
}
