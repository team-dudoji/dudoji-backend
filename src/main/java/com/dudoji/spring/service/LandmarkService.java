package com.dudoji.spring.service;

import com.dudoji.spring.dto.landmark.LandmarkDetectionDto;
import com.dudoji.spring.dto.landmark.LandmarkRequestDto;
import com.dudoji.spring.dto.landmark.LandmarkResponseDto;
import com.dudoji.spring.dto.mapsection.RevealCirclesRequestDto;
import com.dudoji.spring.models.dao.LandmarkDao;
import com.dudoji.spring.models.domain.Landmark;
import com.dudoji.spring.util.BitmapUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LandmarkService {

    @Autowired
    private LandmarkDao landmarkDao;

    public List<LandmarkResponseDto> getLandmarks(long userId) {
        return landmarkDao.getLandmarks(userId)
                .stream()
                .map(LandmarkResponseDto::new)
                .toList();
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
        double deltaLng = Math.toDegrees(radius / BitmapUtil.EARTH_RADIUS * Math.cos(Math.toRadians(revealCircleDto.getLat())));

		return landmarkDao.getLandmarksCircleRadius(userId, lat, lng, deltaLat, deltaLng)
			.stream()
			.map(LandmarkResponseDto::new)
			.toList();
    }
}