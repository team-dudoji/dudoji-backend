package com.dudoji.spring.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.util.Tuple;

@Getter
@Setter
public class Point {
    public static final int BASE_LONGITUDE = 129082255; // 경도 x * 10^6
    public static final int BASE_LATITUDE = 35230853; // 위도 y * 10^6

    private int x, y;

    public Point(int x, int y){
        this.x = x; this.y = y;
    }

    public static Tuple<Integer, Integer> convertRealToXY(int longitude, int latitude){
        int x = Math.floorDiv((longitude - BASE_LONGITUDE), MapSection.MAP_SECTION_WIDTH);
        int y = Math.floorDiv((latitude - BASE_LATITUDE), MapSection.MAP_SECTION_WIDTH);
        return new Tuple<Integer, Integer>(x, y);
    }

    public static Tuple<Integer, Integer> convertXYToReal(int x, int y){
        int longitude = x * MapSection.MAP_SECTION_WIDTH + BASE_LONGITUDE;
        int latitude  = BASE_LATITUDE + MapSection.MAP_SECTION_WIDTH * y;
        return new Tuple<Integer, Integer>(longitude, latitude);
    }

}
