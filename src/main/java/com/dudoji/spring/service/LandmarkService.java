package com.dudoji.spring.service;

import com.dudoji.spring.dto.landmark.LandmarkDetectionDto;
import com.dudoji.spring.dto.landmark.LandmarkRequestDto;
import com.dudoji.spring.dto.landmark.LandmarkResponseDto;
import com.dudoji.spring.models.dao.LandmarkDao;
import com.dudoji.spring.models.domain.Landmark;
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
                landmarkRequestDto.imageUrl(),
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
}