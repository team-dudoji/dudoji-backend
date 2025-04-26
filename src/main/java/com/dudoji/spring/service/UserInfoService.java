package com.dudoji.spring.service;

import com.dudoji.spring.models.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserInfoService
 * This class is responsible for all service logic related to user information.
 *
 * <ul>
 *     <li>Get User Profile Image</li>
 * </ul>
 */
@Service
public class UserInfoService {

    @Autowired
    UserDao userDao;

    public String getProfileImage(long uid) {
        return userDao.getProfileImageUrl(uid);
    }
}
