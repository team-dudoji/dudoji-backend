package com.dudoji.spring.service;

import com.dudoji.spring.dto.pin.HashtagDto;
import com.dudoji.spring.dto.pin.PinRequestDto;
import com.dudoji.spring.dto.pin.PinResponseDto;
import com.dudoji.spring.dto.user.UserSimpleDto;
import com.dudoji.spring.models.dao.FollowDao;
import com.dudoji.spring.models.dao.HashtagDao;
import com.dudoji.spring.models.dao.LikesDao;
import com.dudoji.spring.models.dao.PinDao;
import com.dudoji.spring.models.domain.Pin;
import com.dudoji.spring.util.BitmapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class PinService {

    @Autowired
    private PinDao pinDao;
    @Autowired
    private FollowDao followDao;
    @Autowired
    private LikesDao likesDao;
    @Autowired
    private HashtagDao hashtagDao;

    public PinResponseDto createPin(PinRequestDto pinRequestDto, long userId) {
        Objects.requireNonNull(pinRequestDto, "Pin Request Object cannot be null");
        // TODO: 하루에 개수 제한 넣으려면 여기에 넣어야 합니다.

        Pin pin = pinRequestDto.toDomain(userId);

        long pinId = pinDao.createPin(pin);

        // HashTags
        List<String> hashtags = pinRequestDto.getHashtags();
        hashtags.forEach(hashtag -> {
            hashtagDao.insertOrGetHashtag(hashtag, pinId);
        });

        if (pinId > 0) {
            PinResponseDto pinResponseDto = new PinResponseDto(pin);
            pinResponseDto.setPinId(pinId);
            pinResponseDto.setMaster(PinResponseDto.Who.MINE);
            pinResponseDto.setLikeCount(0);
            pinResponseDto.setLiked(false);
            pinResponseDto.setAddress(pin.getAddress());
            pinResponseDto.setPlaceName(pin.getPlaceName());
            pinResponseDto.setPinSkinId(pin.getPinSkinId());
            pinResponseDto.setHashtags(hashtags);

            return pinResponseDto;
        }
        return null; // TODO: ERROR
    }

    /**
     * Get List of PinResponseDto with square range.
     *
     * @param radius    radius
     * @param centerLat lat value of center
     * @param centerLng lng value of center
     * @param userId Id of who want to get pin list.
     * @return List of PinResponseDto
     */
    public List<PinResponseDto> getClosePins (
            double radius, double centerLat, double centerLng, long userId, int limit, int offset
    ) {
        double deltaLat = Math.toDegrees(radius / BitmapUtil.EARTH_RADIUS);
        double deltaLng = Math.toDegrees(radius / BitmapUtil.EARTH_RADIUS / Math.cos(Math.toRadians(centerLat)));

        double minLat = centerLat - deltaLat;
        double maxLat = centerLat + deltaLat;
        double minLng = centerLng - deltaLng;
        double maxLng = centerLng + deltaLng;

        List<Pin> pinList = pinDao.getClosePins(minLat, minLng, maxLat, maxLng, limit, offset);

        return createResponsDtoList(pinList, userId);
    }

    public List<PinResponseDto> getMyPins(long userId, int limit, int offset) {
        List<Pin> pins = pinDao.getALlPinsByUserId(userId, limit, offset);

        return createResponsDtoList(pins, userId);
    }

    public boolean likePin(long userId, long pinId) {
        return likesDao.likePin(userId, pinId);
    }

    public boolean unlikePin(long userId, long pinId) {
        return likesDao.unlikePin(userId, pinId);
    }

    public int getLikesCount(long pinId) {
        return likesDao.getLikesCount(pinId);
    }

    public boolean isLiked(long userId, long pinId) {
        return likesDao.isLiked(userId, pinId);
    }

    public void refreshLikes() {
        likesDao.refreshViews();
    }

    private List<PinResponseDto> createResponsDtoList(List<Pin> pinList, long userId) {
        List<Long> pinIds = pinList.stream()
            .map(Pin::getPinId)
            .toList();

        Set<Long> followingSet = followDao.getFollowingListByUser(userId)
            .stream()
            .map(UserSimpleDto::id).collect(Collectors.toSet());

        Set<Long> likedSet;
        if (!pinIds.isEmpty()) {
            likedSet = likesDao.getLikedSet(userId, pinIds);
        } else {
            likedSet = Collections.emptySet();
        }

        Map<Long, List<String>> hashtagsMap = hashtagDao.getHashtagByPinIds(pinIds)
            .stream()
            .collect(Collectors.groupingBy(
                HashtagDto::pinId,
                Collectors.mapping(HashtagDto::content, Collectors.toList())
            ));

        return pinList.stream()
            .map(pin -> {
                PinResponseDto dto = new PinResponseDto(pin);
                long pinUserId = pin.getUserId();

                PinResponseDto.Who who = (pinUserId == userId)                     ? PinResponseDto.Who.MINE
                    : followingSet.contains(pinUserId) ? PinResponseDto.Who.FOLLOWING
                    : PinResponseDto.Who.UNKNOWN;

                dto.setMaster(who);
                dto.setLiked(
                    likedSet.contains(pin.getPinId())
                );
                dto.setHashtags(
                    hashtagsMap.get(pin.getPinId())
                );
                return dto;
            })
            .toList();
    }
}
