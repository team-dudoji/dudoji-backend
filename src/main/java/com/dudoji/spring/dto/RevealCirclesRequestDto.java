package com.dudoji.spring.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RevealCirclesRequestDto {
    List<RevealCircleDto> revealCircles;

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (RevealCircleDto circleDto : revealCircles) {
            builder.append("{").append(circleDto.toString()).append("} ");
        }
        return builder.toString();
    }

    @Setter
    @Getter
    public static class RevealCircleDto {
        private double lat;
        private double lng;
        private double radius;

        @Override
        public String toString() {
            return String.format("lat: %.2f, lng: %.2f, radius: %.2f", lat, lng, radius);
        }
    }
}
