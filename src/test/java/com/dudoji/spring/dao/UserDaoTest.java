package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({UserDao.class})
public class UserDaoTest extends DBTestBase {

    @Autowired
    UserDao userDao;

    String userEmail = "tester@test.com";
    String profileImageUrl = "https://img";

    User user = User.builder()
            .name("tester")
            .email(userEmail)
            .password("pw")
            .role("user")
            .profileImageUrl(profileImageUrl)
            .build();
    @Test
    void createAndFetch() {
        long newId = userDao.createUserByUser(user);
        User fetchedUser = userDao.getUserById(newId);
        User fetchedUser2 = userDao.getUserByEmail(userEmail);

        assertEquals(fetchedUser.getId(), fetchedUser2.getId()); // Same User

        assertEquals(fetchedUser.getEmail(), userEmail);
        assertEquals(fetchedUser2.getEmail(), userEmail);

        assertEquals(fetchedUser.getId(), newId);
        assertEquals(fetchedUser2.getId(), newId);

        assertEquals(profileImageUrl, fetchedUser.getProfileImageUrl());
        assertEquals(profileImageUrl, userDao.getProfileImageUrl(newId));
    }

    @Test
    void deleteOne() {
        long newId = userDao.createUserByUser(user);

        boolean result = userDao.removeUserById(newId);
        assertTrue(result);
    }

    @Test
    void recommendedTest() {
        List<String> userTestEmailList = new ArrayList<>();
        for (int i=0; i<5; i++) {
            String userTestEmail = "rapidtest@rapid.com" + i;
            User user = User.builder()
                    .name("tester" + i)
                    .email(userTestEmail)
                    .password("pw")
                    .role("user")
                    .profileImageUrl(profileImageUrl)
                    .build();
            userDao.createUserByUser(user);
            userTestEmailList.add(userTestEmail);
        }

        List<User> userList = userDao.getRecommendedUsers("rapid");

        userList.forEach(user -> {
            assertTrue(userTestEmailList.contains(user.getEmail()));
        });
    }
}
