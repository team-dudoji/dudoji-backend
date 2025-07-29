package com.dudoji.spring.service;

import com.dudoji.spring.dto.pin.PinResponseDto;
import com.dudoji.spring.models.dao.FollowDao;
import com.dudoji.spring.models.dao.LikesDao;
import com.dudoji.spring.models.dao.PinDao;
import com.dudoji.spring.models.domain.Pin;
import com.dudoji.spring.util.BitmapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PinService {

    @Autowired
    private PinDao pinDao;
    @Autowired
    private FollowDao followDao;
    @Autowired
    private LikesDao likesDao;

    public PinResponseDto createPin (Pin pin) {
        Objects.requireNonNull(pin, "Pin cannot be null");
        // TODO: 하루에 개수 제한 넣으려면 여기에 넣어야 합니다.

        long id = pinDao.createPin(pin);
        if (id > 0) {
            PinResponseDto pinResponseDto = new PinResponseDto(pin);
            pinResponseDto.setPinId(id);
            pinResponseDto.setMaster(PinResponseDto.Who.MINE);
            pinResponseDto.setLikeCount(0);
            pinResponseDto.setLiked(false);
            pinResponseDto.setAddress(pin.getAddress());
            pinResponseDto.setPlaceName(pin.getPlaceName());

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

        List<Pin> pinList = pinDao.getClosePins(minLat, maxLat, minLng, maxLng, limit, offset);
		// set likes

		//        for (Pin pin : pinList) {
//            // 3가지로 분류.
//            PinResponseDto temp = new PinResponseDto(pin);
//            long pinUserId = pin.getUserId();
//            if (pinUserId == userId) {
//                temp.setMaster(PinResponseDto.Who.MINE);
//            }
//            else {
//                if (followDao.isFollowing(userId, pinUserId)) {
//                    temp.setMaster(PinResponseDto.Who.FOLLOWING);
//                }
//                else {
//                    temp.setMaster(PinResponseDto.Who.UNKNOWN);
//                }
//            }
//            pinResponseDtoList.add(temp);
//        }

        return pinList.stream()
                .map(pin -> {
                    PinResponseDto dto = new PinResponseDto(pin);
                    long pinUserId = pin.getUserId();

                    PinResponseDto.Who who = (pinUserId == userId)                     ? PinResponseDto.Who.MINE
                                    : followDao.isFollowing(userId, pinUserId) ? PinResponseDto.Who.FOLLOWING
                                                                               : PinResponseDto.Who.UNKNOWN;
                    dto.setMaster(who);
                    dto.setLiked(
                            isLiked(userId, pin.getPinId())
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PinResponseDto> getMyPins(long userId, int limit, int offset) {
        List<Pin> pins = pinDao.getALlPinsByUserId(userId, limit, offset);

		return pins.stream()
				.map(pin -> {
					PinResponseDto dto = new PinResponseDto(pin);
					dto.setMaster(PinResponseDto.Who.MINE);
					dto.setLiked(isLiked(pin.getUserId(), pin.getPinId()));
					return dto;
				})
				.collect(Collectors.toList());
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
}
