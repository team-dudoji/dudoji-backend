package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.dto.UserWalkDistancesDto;
import com.dudoji.spring.models.dao.UserWalkDistanceDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import({UserWalkDistanceDao.class})
public class UserWalkDistanceDaoTest extends DBTestBase {

    @Autowired
    UserWalkDistanceDao userWalkDistanceDao;

    long uid = 101L;
    LocalDate date = LocalDate.now();

    @Test
    void createAndFetch() {
        int distance = 5000;

        boolean created = userWalkDistanceDao.createUserWalkDistance(uid, date, distance);

        assertTrue(created);
        UserWalkDistancesDto.UserWalkDistanceDto test = userWalkDistanceDao
                                                            .getUserWalkDistanceByIdOnDuration(uid, date, date)
                                                            .getUserWalkDistances().getFirst();

        assertThat(test.getDistance()).isEqualTo(5000);
        assertThat(test.getDate()).isEqualTo(date);
    }

    @Test
    void deleteOne() {
        userWalkDistanceDao.createUserWalkDistance(uid, date, 5000);
        boolean result = userWalkDistanceDao.deleteUserWalkDistance(uid, date, date);

        assertTrue(result);
        assertTrue(userWalkDistanceDao.getUserWalkDistanceByIdOnDuration(uid, date, date).getUserWalkDistances().isEmpty());
    }

    @Test
    void deleteRange() {
        LocalDate start = date.minusDays(1);
        LocalDate end = date.plusDays(1);

        userWalkDistanceDao.createUserWalkDistance(uid, start, 5);
        userWalkDistanceDao.createUserWalkDistance(uid, end, 5);
        boolean result = userWalkDistanceDao.deleteUserWalkDistance(uid, start, end);
        assertTrue(result);
        assertTrue(userWalkDistanceDao.getUserWalkDistanceByIdOnDuration(uid, start, end).getUserWalkDistances().isEmpty());
    }
}
