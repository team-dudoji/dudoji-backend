package com.dudoji.spring.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RevealCirclesRequestDto {
    List<PositionDto> positions;

    @Setter
    @Getter
    public static class PositionDto {
        private double lat;
        private double lng;
        private double radius;
    }
}
