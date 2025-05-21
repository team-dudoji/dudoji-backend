package com.dudoji.spring.service;

import com.dudoji.spring.dto.PinDto;
import com.dudoji.spring.models.dao.FollowDao;
import com.dudoji.spring.models.dao.PinDao;
import com.dudoji.spring.models.domain.Pin;
import com.dudoji.spring.util.BitmapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PinService {

    @Autowired
    private PinDao pinDao;
    @Autowired
    private FollowDao followDao;

    public void createPin (Pin pin) {
        Objects.requireNonNull(pin, "Pin cannot be null");
        if (pin.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        // TODO: 하루에 개수 제한 넣으려면 여기에 넣어야 합니다.

        pinDao.createPin(pin);
        log.info("User {} create a new pin with title {}", pin.getUserId(), pin.getTitle());
    }

    /**
     * Get List of PinDto with square range.
     *
     * @param radius    radius
     * @param centerLat lat value of center
     * @param centerLng lng value of center
     * @param userId Id of who want to get pin list.
     * @return List of PinDto
     */
    public List<PinDto> getClosePins (
            double radius, double centerLat, double centerLng, long userId
    ) {
        double deltaLat = Math.toDegrees(radius / BitmapUtil.EARTH_RADIUS);
        double deltaLng = Math.toDegrees(radius / BitmapUtil.EARTH_RADIUS / Math.cos(Math.toRadians(centerLat)));

        double minLat = centerLat - deltaLat;
        double maxLat = centerLat + deltaLat;
        double minLng = centerLng - deltaLng;
        double maxLng = centerLng + deltaLng;

        List<Pin> pinList = pinDao.getClosePins(minLat, maxLat, minLng, maxLng);
        List<PinDto> pinDtoList = new ArrayList<>();

        for (Pin pin : pinList) {
            // 3가지로 분류.
            PinDto temp = new PinDto(pin);
            long pinUserId = pin.getUserId();
            if (pinUserId == userId) {
                temp.setMaster(PinDto.Who.MINE);
            }
            else {
                if (followDao.isFollowing(userId, pinUserId)) {
                    temp.setMaster(PinDto.Who.FOLLOWING);
                }
                else {
                    temp.setMaster(PinDto.Who.UNKNOWN);
                }
            }
            pinDtoList.add(temp);
        }

        return pinDtoList;
    }
}
