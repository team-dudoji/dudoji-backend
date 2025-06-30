package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.models.dao.LikesDao;
import com.dudoji.spring.models.dao.PinDao;
import com.dudoji.spring.models.domain.Pin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import({LikesDao.class, PinDao.class})
public class LikesDaoTest extends DBTestBase {

    @Autowired
    LikesDao likesDao;
    @Autowired
    PinDao pinDao;

    long userId = 101L;
    Pin newPin = Pin.builder()
            .userId(userId)
            .lat(1)
            .lng(1)
            .content("content")
            .imageUrl("imageUrl")
            .placeName("placeName")
            .createdDate(LocalDateTime.now())
            .address("address")
            .build();

    @Test
    void createAndFetch() {
        long pinId = pinDao.createPin(newPin);

        boolean likePin = likesDao.likePin(userId, pinId);
        assertTrue(likePin);

        likesDao.refreshViews();
        int likesCount = likesDao.getLikesCount(pinId);
        assertEquals(1, likesCount);

        boolean isLiked = likesDao.isLiked(userId, pinId);
        assertTrue(isLiked);

        boolean unlikePin = likesDao.unlikePin(userId, pinId);
        assertTrue(unlikePin);

        likesDao.refreshViews();
        likesCount = likesDao.getLikesCount(pinId);
        assertEquals(0, likesCount);
    }

}
