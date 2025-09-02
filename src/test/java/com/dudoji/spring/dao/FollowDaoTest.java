package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.dto.user.UserSimpleDto;
import com.dudoji.spring.models.dao.FollowDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
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

        List<UserSimpleDto> followerListByUser = followDao.getFollowerListByUser(102, 10000, 0);

        List<Long> followerIdList = List.of(101L, 103L, 104L);

        assertEquals(3, followerListByUser.size());
        Set<Long> followerIdListFromDB = followerListByUser.stream()
            .map(UserSimpleDto::id)
            .collect(Collectors.toSet());
        assertTrue(followerIdListFromDB.containsAll(followerIdList));
    }

    @Test
    void followCreatedTest() {
        followDao.createFollowingByUser(101, 102); // 서로 팔로우
        followDao.createFollowingWithSelectingDay(101, 103, LocalDate.now().minusDays(20L)); // 서로 팔로우
        followDao.createFollowingWithSelectingDay(101, 104, LocalDate.now().minusDays(10L)); // 서로 팔로우
        followDao.createFollowingByUser(101, 105); // Not 팔로우
        followDao.createFollowingByUser(101, 106); // Not 팔로우

        followDao.createFollowingByUser(102, 101);
        followDao.createFollowingByUser(103, 101);
        followDao.createFollowingByUser(104, 101);

        List<UserSimpleDto> followingListByUser = followDao.getFollowingListByUser(101);

        System.out.printf("%-5s %-15s %-25s %-30s %-15s %-15s%n",
            "ID", "Name", "Email", "Profile", "FollowingAt", "FollowedAt");
        System.out.println("-----------------------------------------------------------------------------------------------");

        followingListByUser.forEach(user -> {
            System.out.printf("%-5d %-15s %-25s %-30s %-15s %-15s%n",
                user.id(),
                user.name(),
                user.email(),
                user.profileImageUrl(),
                user.followingAt(),
                user.followedAt()
            );
        });

        System.out.println("-----------------------------------------------------------------------------------------------\n\n\n");

        List<UserSimpleDto> followerListByUser = followDao.getFollowerListByUser(101);
        System.out.printf("%-5s %-15s %-25s %-30s %-15s %-15s%n",
            "ID", "Name", "Email", "Profile", "FollowingAt", "FollowedAt");
        System.out.println("-----------------------------------------------------------------------------------------------");

        followerListByUser.forEach(user -> {
            System.out.printf("%-5d %-15s %-25s %-30s %-15s %-15s%n",
                user.id(),
                user.name(),
                user.email(),
                user.profileImageUrl(),
                user.followingAt(),
                user.followedAt()
            );
        });

    }
}
