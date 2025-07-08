package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.dto.user.UserSimpleDto;
import com.dudoji.spring.models.dao.FollowDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        boolean result = followDao.createFollowingByUser(101, 102);
        assertTrue(result);

        assertTrue(followDao.isFollowing(101, 102));
        List<UserSimpleDto> getFollowerListByUser = followDao.getFollowingListByUser(101);
        assertEquals(1, getFollowerListByUser.size());

        boolean result2 = followDao.deleteFollowingByUser(101, 102);
        assertTrue(result2);

        List<UserSimpleDto> getFollowerListByUserAfterDelete = followDao.getFollowingListByUser(101);
        assertTrue(getFollowerListByUserAfterDelete.isEmpty());

    }

    @Test
    void followerTest() {
        followDao.createFollowingByUser(101, 102);
        followDao.createFollowingByUser(103, 102);
        followDao.createFollowingByUser(104, 102);

        List<UserSimpleDto> followerListByUser = followDao.getFollowerListByUser(102);

        List<Long> followerIdList = List.of(101L, 103L, 104L);

        assertEquals(3, followerListByUser.size());
        Set<Long> followerIdListFromDB = followerListByUser.stream()
            .map(UserSimpleDto::id)
            .collect(Collectors.toSet());
        assertTrue(followerIdListFromDB.containsAll(followerIdList));
    }
}
