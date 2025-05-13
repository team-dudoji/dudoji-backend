package com.dudoji.spring.service;

import com.dudoji.spring.models.dao.FriendDao;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendService {

    @Autowired
    private FriendDao friendDao;
    @Autowired
    private UserDao userDao;

    public List<User> getFriendsById(long userId) {
        List<Long> userIdList = friendDao.getFriendListByUser(userId);
        List<User> userList = new ArrayList<>(userIdList.size());
        for (Long friendId : userIdList) {
            userList.add(userDao.getUserById(friendId));
        }

        return userList;
    }

    public boolean createFriendById(long userId, long friendId) {
        return friendDao.createFriendByUser(userId, friendId);
    }

    public boolean deleteFriend(long userId, long friendId) {
        return friendDao.deleteFriendByUser(userId, friendId);
    }

    public List<User> getRecommendedFriends(String email) {
        return userDao.getRecommendedUsers(email);
    }

    public List<User> getFriendRequestList(long userId) {
        List<Long> requestIdList = friendDao.getFriendRequestList(userId);
        List<User> userList = new ArrayList<>(requestIdList.size());
        for (Long requestId : requestIdList) {
            userList.add(userDao.getUserById(requestId));
        }
        return userList;
    }
    public boolean acceptFriend(long senderId, long userId) {
        return friendDao.acceptFriend(senderId, userId);
    }

    public boolean rejectFriend(long senderId, long userId) {
        return friendDao.rejectFriend(senderId, userId);
    }

    public boolean requestFriend(long senderId, long userId) {
        return friendDao.requestFriend(senderId, userId);
    }
}
