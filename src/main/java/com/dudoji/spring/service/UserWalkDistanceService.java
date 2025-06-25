package com.dudoji.spring.service;

import com.dudoji.spring.dto.UserWalkDistancesDto;
import com.dudoji.spring.models.dao.UserWalkDistanceDao;
import com.dudoji.spring.models.domain.UserWalkDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
/**
 * This Class apply user step with using user_step DAO.
 */
public class UserWalkDistanceService {

    @Autowired
    UserWalkDistanceDao userWalkDistanceDao;

    /**
     * Apply User Step to Database with param
     * @param uid user id
     * @param distanceDate target date
     * @param distanceMeter count
     * @return true when success, false or error when fail
     */
    public boolean applyUserWalkDistance(long uid, LocalDate distanceDate, int distanceMeter) {
        return userWalkDistanceDao.createUserWalkDistance(uid, distanceDate, distanceMeter);
    }

    /**
     * Get User Step One Day
     * @param uid user id
     * @param distanceDate target date
     * @return UserWalkDistanceDto Object that contains user step
     */
    @Deprecated(since = "No Using Single Day")
    public UserWalkDistance getUserWalkDistanceByIdAndDate(long uid, LocalDate distanceDate) {
        return userWalkDistanceDao.getUserWalkDistanceByIdOnDate(uid, distanceDate);
    }

    /**
     * Get User Steps by Given Duration
     * @param uid user id
     * @param startDate Start Date of Duration
     * @param endDate End Date of Duration
     * @return List of UserWalkDistanceDto comes from Data base
     */
    public UserWalkDistancesDto getUserWalkDistanceByIdAndDuration(long uid, LocalDate startDate, LocalDate endDate) {
        return userWalkDistanceDao.getUserWalkDistanceByIdOnDuration(uid, startDate, endDate);
    }
}
