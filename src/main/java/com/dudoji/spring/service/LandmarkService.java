package com.dudoji.spring.service;

import com.dudoji.spring.dto.landmark.LandmarkDetectionDto;
import com.dudoji.spring.dto.landmark.LandmarkRequestDto;
import com.dudoji.spring.dto.landmark.LandmarkResponseDto;
import com.dudoji.spring.dto.mapsection.RevealCirclesRequestDto;
import com.dudoji.spring.models.dao.FestivalRepository;
import com.dudoji.spring.models.dao.HashtagDao;
import com.dudoji.spring.models.dao.LandmarkDao;
import com.dudoji.spring.models.dao.PinDao;
import com.dudoji.spring.models.domain.Landmark;
import com.dudoji.spring.models.domain.Pin;
import com.dudoji.spring.util.BitmapUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class LandmarkService {

    public static final int MAX_LANDMARK_SIZE = 10;
    public static final int LANDMARK_PIN_RADIUS = 3000;

    private final LandmarkDao landmarkDao;
    private final HashtagDao hashtagDao;
    private final PinDao pinDao;
    private final FestivalRepository festivalRepository;

    public List<LandmarkResponseDto> getLandmarks(long userId) {
        return getLandmarkWithHashtag(injectFestivalToLandmark(landmarkDao.getLandmarks(userId)));
    }

    public void saveLandmark(LandmarkRequestDto landmarkRequestDto) {
        landmarkDao.saveLandmark(
                landmarkRequestDto.lat(),
                landmarkRequestDto.lng(),
                landmarkRequestDto.content(),
                landmarkRequestDto.mapImageUrl(),
                landmarkRequestDto.detailImageUrl(),
                landmarkRequestDto.placeName(),
                landmarkRequestDto.address()
        );
    }

    public void saveLandmarkDetection(LandmarkDetectionDto landmarkDetectionDto, long userId){
        landmarkDao.setDetect(userId, landmarkDetectionDto.landmarkId());
    }

    public void deleteLandmark(Long landmarkId) {
        landmarkDao.deleteLandmark(landmarkId);
    }

    public void putLandmark(Long landmarkId, LandmarkRequestDto landmarkRequestDto) {
        landmarkDao.putLandmark(new Landmark(landmarkId, landmarkRequestDto));
    }

    /**
     * 주어진 좌표에 기반하여 Bounding Box 를 구성하여 정보를 받아오는 함수
     * @param revealCircleDto 원하는 곳의 좌표를 담은 곳
     * @return List - LandmarkResponseDto
     */
    public List<LandmarkResponseDto> getLandmarksCircleRadius(long userId, RevealCirclesRequestDto.RevealCircleDto revealCircleDto) {

        // Calculate lat, lng value based on radius
        double lat = revealCircleDto.getLat();
        double lng = revealCircleDto.getLng();
        double radius = revealCircleDto.getRadius();

        double deltaLat = Math.toDegrees(radius / BitmapUtil.EARTH_RADIUS);
        double deltaLng = Math.toDegrees(radius / BitmapUtil.EARTH_RADIUS * Math.cos(Math.toRadians(lat)));
		return getLandmarkWithHashtag(
                injectFestivalToLandmark(landmarkDao.getLandmarksCircleRadius(userId, lat, lng, deltaLat, deltaLng)));
    }

    public List<LandmarkResponseDto> getLandmarksByKeyword(String keyword) {
        return getLandmarkWithHashtag(injectFestivalToLandmark(landmarkDao.getLandmarksByKeyword(keyword)));
        // TODO: 맥스 사이즈가 왜 있지?
    }

    public List<Landmark> injectFestivalToLandmark(List<Landmark> landmarks) {
        LocalDate today = LocalDate.of(2025, 9, 27);
        landmarks.forEach(
                landmark -> {
                    var festival = festivalRepository.findFirstByLandmarkIdAndDate(landmark.getLandmarkId(), today);
                    if (!festival.isEmpty()) {
                        landmark.setFestival(festival.getFirst());
                    }
                }

        );
        return landmarks;
    }

    public List<LandmarkResponseDto> getLandmarkWithHashtag(List<Landmark> landmarkResponseDtoList) {
		// near Pin List
		return landmarkResponseDtoList
			.stream()
			.map(LandmarkResponseDto::new)
			.peek(dto -> {
				double landmarkLat = dto.getLat();
				double landmarkLng = dto.getLng();

				double dLat = Math.toDegrees(LANDMARK_PIN_RADIUS / BitmapUtil.EARTH_RADIUS);
				double dLng = Math.toDegrees(LANDMARK_PIN_RADIUS / BitmapUtil.EARTH_RADIUS * Math.cos(Math.toRadians(landmarkLat)));

				Map<String, Integer> tags = hashtagDao.getHashtagCountByPinIds(
					// near Pin List
					pinDao.getClosePins(landmarkLat - dLat, landmarkLng - dLng, landmarkLat + dLat, landmarkLng + dLng, Integer.MAX_VALUE, 0)
						.stream()
						.map(Pin::getPinId)
						.toList()
				);
				dto.setHashtags(new ArrayList<>(tags.keySet()));
			})
			.toList();
    }
}