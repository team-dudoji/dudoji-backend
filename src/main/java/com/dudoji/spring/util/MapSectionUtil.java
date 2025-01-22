package com.dudoji.spring.util;

import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Point;
import org.yaml.snakeyaml.util.Tuple;

import java.util.List;

public class MapSectionUtil {
    // TODO - apply user positions to MapSection

    public static MapSection applyPosition(MapSection mapSection, int lng, int lat) {
        // TODO
        // GPS 받아온 위도 경도를 MapSection에 적용한다. (Bitmap Util을 활용)
        // return은 적용한 MapSection
        return null;
    }

    public static MapSection applyPositions(MapSection mapSection, List<Tuple<Integer, Integer>> positions) {
        // TODO
        // mapSection에서 받아온 위도 경도들를 MapSection에 적용한다.
        // return은 적용한 MapSection
        return null;
    }

    public static List<Point> getMapSectionPointsByPosition(int lng, int lat, float radius) {
        // TODO
        // 위도 경도, 범위를 받아와서 거기에 곂치는 MapSections들의 좌표를 반환한다.
         return null;
    }
}
