package com.dudoji.spring.dto.landmark;

import java.util.List;
import com.dudoji.spring.config.LandmarkConfig;
import com.dudoji.spring.dto.festival.FestivalResponseDto;
import com.dudoji.spring.models.domain.Landmark;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LandmarkResponseDto{
	private long    landmarkId;
	private double  lat;
	private double  lng;
	private String  placeName;
	private String  address;
	private String  content;
	private String  mapImageUrl;
	private String  detailImageUrl;
	private double  radius;
	private boolean isDetected;
	private List<String> hashtags;
    private FestivalResponseDto festivalInfo;


    public LandmarkResponseDto(Landmark landmark) {
        this(
            landmark.getLandmarkId(),
            landmark.getLat(),
            landmark.getLng(),
            landmark.getPlaceName(),
            landmark.getAddress(),
            landmark.getContent(),
            landmark.getMapImageUrl(),
            landmark.getDetailImageUrl(),
            landmark.isDetected() ? LandmarkConfig.LANDMARK_DETECTED_RADIUS : LandmarkConfig.LANDMARK_UNDETECTED_RADIUS,
            landmark.isDetected(),
            List.of(), // 기본값 null
            FestivalResponseDto.from(landmark.getFestival())
        );
	}
}
