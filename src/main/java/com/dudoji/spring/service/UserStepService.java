package com.dudoji.spring.service;

import com.dudoji.spring.models.dao.UserStepDao;
import com.dudoji.spring.models.domain.UserStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
/**
 * This Class apply user step with using user_step DAO.
 */
public class UserStepService {

    @Autowired
    UserStepDao userStepDao;

    /**
     * Apply User Step to Database with param
     * @param uid user id
     * @param step_date target date
     * @param step_count count
     * @return true when success, false or error when fail
     */
    public boolean applyUserStep(long uid, LocalDate step_date, int step_count) {
        return userStepDao.createUserStep(uid, step_date, step_count);
    }

    /**
     * Get User Step One Day
     * @param uid user id
     * @param step_date target date
     * @return UserStep Object that contains user step
     */
    public UserStep getUserStepByIdAndDate(long uid, LocalDate step_date) {
        return userStepDao.getUserStepByIdOnDate(uid, step_date);
    }

    /**
     * Get User Steps by Given Duration
     * @param uid user id
     * @param startDate Start Date of Duration
     * @param endDate End Date of Duration
     * @return List of UserStep comes from Data base
     */
    public List<UserStep> getUserStepsByIdAndDuration(long uid, LocalDate startDate, LocalDate endDate) {
        return userStepDao.getUserStepByIdOnDuration(uid, startDate, endDate);
    }
}
