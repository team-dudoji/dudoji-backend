package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.models.dao.FollowDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@JdbcTest
@Import({FollowDao.class})
public class FollowDaoTest extends DBTestBase {

    @Autowired
    FollowDao followDao;


    @Test
    void createFollowAndCheck() {
        // 102가 101 팔로우
        boolean result = followDao.createFollowByUser(101, 102);
        assertTrue(result);

        assertTrue(followDao.isFollowing(101, 102));
        List<Long> getFollowerListByUser = followDao.getFollowerListByUser(101);
        assertEquals(1, getFollowerListByUser.size());

        boolean result2 = followDao.deleteFollowByUser(101, 102);
        assertTrue(result2);

        List<Long> getFollowerListByUserAfterDelete = followDao.getFollowerListByUser(101);
        assertTrue(getFollowerListByUserAfterDelete.isEmpty());

    }
}
