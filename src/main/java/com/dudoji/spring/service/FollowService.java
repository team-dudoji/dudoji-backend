package com.dudoji.spring.service;

import com.dudoji.spring.dto.pin.PinResponseDto;
import com.dudoji.spring.dto.user.UserSimpleDto;
import com.dudoji.spring.models.dao.FollowDao;
import com.dudoji.spring.models.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class FollowService {

    @Autowired
    private FollowDao followDao;
    @Autowired
    private UserDao userDao;

    public List<UserSimpleDto> getFollowingById(long userId, int page, int size) {
        return followDao.getFollowingListByUser(userId, page, size);
    }

    public List<UserSimpleDto> getFollowerById(long userId, int page, int size) {
        return followDao.getFollowerListByUser(userId, page, size);
    }

    public boolean createFollowing(long userId, long followeeId) {
        return followDao.createFollowingByUser(userId, followeeId);
    }

    public boolean deleteFollowing(long userId, long followeeId) {
        return followDao.deleteFollowingByUser(userId, followeeId);
    }

    public List<UserSimpleDto> getRecommendedFollow(String email) {
        return userDao.getRecommendedUsers(email)
                .stream()
                .map(UserSimpleDto::new)
                .toList();
    }

    public List<UserSimpleDto> getUsers (
        long userId,
        String type,
        String keyword,
        Pageable pageable
    ) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        int offset = Math.max(0, page) * Math.max(1, size);
        int limit = size;
        Sort sort = pageable.getSort();

        return followDao.getUsers(userId, offset, limit, sort, keyword, type);
    }

    public int countFollowers(long userId) {
        return followDao.getNumOfFollowerByUserId(userId);
    }

    public int countFollowing(long userId) {
        return followDao.getNumOfFollowingByUserId(userId);
    }
    /**
     * &#064;Deprecated
     * When Using Secret Account, Can be reactivated.
     */
    /*
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
     */
}
