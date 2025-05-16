package com.dudoji.spring.service;

import com.dudoji.spring.models.dao.FollowDao;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowService {

    @Autowired
    private FollowDao followDao;
    @Autowired
    private UserDao userDao;

    public List<User> getFollowersById(long userId) {
        List<Long> userIdList = followDao.getFollowerListByUser(userId);
        List<User> userList = new ArrayList<>(userIdList.size());
        for (Long friendId : userIdList) {
            userList.add(userDao.getUserById(friendId));
        }

        return userList;
    }

    public boolean createFollowById(long userId, long followeeId) {
        return followDao.createFollowByUser(userId, followeeId);
    }

    public boolean deleteFollow(long userId, long followeeId) {
        return followDao.deleteFollowByUser(userId, followeeId);
    }

    public List<User> getRecommendedFollow(String email) {
        return userDao.getRecommendedUsers(email);
    }


    /**
     * &#064;Deprecated
     * When Using Secret Account, Can be reactivated.
     */
    @Deprecated
    public List<User> getFriendRequestList(long userId) {
        List<Long> requestIdList = followDao.getFollowRequestList(userId);
        List<User> userList = new ArrayList<>(requestIdList.size());
        for (Long requestId : requestIdList) {
            userList.add(userDao.getUserById(requestId));
        }
        return userList;
    }
    @Deprecated
    public boolean acceptFriend(long senderId, long userId) {
        return followDao.acceptFollow(senderId, userId);
    }
    @Deprecated
    public boolean rejectFriend(long senderId, long userId) {
        return followDao.rejectFollow(senderId, userId);
    }
    @Deprecated
    public boolean requestFriend(long senderId, long userId) {
        return followDao.requestFollow(senderId, userId);
    }
}
