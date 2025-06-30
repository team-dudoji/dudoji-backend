package com.dudoji.spring.service;

import com.dudoji.spring.dto.pin.PinResponseDto;
import com.dudoji.spring.models.dao.LandmarkDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LandmarkService {

    @Autowired
    private LandmarkDao landmarkDao;

    public List<PinResponseDto> getLandmarks() {
        return null;
    }

    public List<PinResponseDto> getDetectedLandmarks() {
        return null;
    }
}