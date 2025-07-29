package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.models.dao.PinDao;
import com.dudoji.spring.models.domain.Pin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({PinDao.class})
public class PinDaoTest extends DBTestBase {

    @Autowired
    PinDao pinDao;

    long uid = 101L;
    double lat = 1.0;
    double lng = 1.0;

    @Test
    void createPin() {
        Pin newPin = Pin.builder()
                .userId(uid)
                .lat(lat)
                .lng(lng)
                .content("content")
                .imageUrl("imageUrl")
                .placeName("placeName")
                .createdDate(LocalDateTime.now())
                .address("address")
                .build();

        long id = -1L;
        id = pinDao.createPin(newPin);

        assertNotEquals(-1L, id);
    }

    @Test
    void findPin() {
        for (double i=0.1; i<0.3; i += 0.1) {
            Pin newPin = Pin.builder()
                    .userId(uid)
                    .lat(lat + i)
                    .lng(lng)
                    .content("content")
                    .imageUrl("imageUrl")
                    .placeName("placeName")
                    .createdDate(LocalDateTime.now())
                    .address("address")
                    .build();
            pinDao.createPin(newPin);
        }

        List<Pin> pinList = pinDao.getClosePins(
                lat, lng,
                lat + 0.3, lng + 0.3,
            100, 0
        );

        assertEquals(2, pinList.size());

        List<Pin> pinListByUserId = pinDao.getALlPinsByUserId(uid, 100, 0);
        assertEquals(2, pinListByUserId.size());
    }
}
