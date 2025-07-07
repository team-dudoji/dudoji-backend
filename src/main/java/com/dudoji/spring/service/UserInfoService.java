package com.dudoji.spring.service;

import com.dudoji.spring.dto.user.UserProfileDto;
import com.dudoji.spring.models.dao.FollowDao;
import com.dudoji.spring.models.dao.PinDao;
import com.dudoji.spring.models.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserInfoService
 * This class is responsible for all service logic related to user information.
 *
 * <ul>
 *     <li>Get User Profile Image</li>
 * </ul>
 */
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserDao userDao;
    private final PinDao pinDao;
    private final FollowDao followDao;

    public String getProfileImage(long uid) {
        return userDao.getUserById(uid).getProfileImageUrl();
    }

    public String getUsername(long uid) {
        return userDao.getUserById(uid).getName();
    }

    public String getEmail(long uid) {
        return userDao.getUserById(uid).getEmail();
    }

    public UserProfileDto getUserProfileById(long uid) {
        return new UserProfileDto(
                userDao.getUserById(uid),
                pinDao.getNumOfPinByUserId(uid),
                followDao.getNumOfFollowerByUserId(uid),
                followDao.getNumOfFollowingByUserId(uid)
        );
    }
}
