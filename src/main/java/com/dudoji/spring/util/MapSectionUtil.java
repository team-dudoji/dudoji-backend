package com.dudoji.spring.util;

import com.dudoji.spring.models.domain.DetailedMapSection;
import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Pair;
import com.dudoji.spring.models.domain.Point;
import org.yaml.snakeyaml.util.Tuple;

import java.util.List;

public class MapSectionUtil {
    // TODO - apply user positions to MapSection

    public static void applyPosition(MapSection mapSection, Point point, double radiusMeters) {
        if (mapSection instanceof DetailedMapSection detailedMapSection) {
            BitmapUtil.setCloseBits(detailedMapSection.getBitmap(), point, radiusMeters); // 기본 줌 사용
        }
    }

    public static void applyPostionWithBit(MapSection mapSection, int bitX, int bitY) {
        if (mapSection instanceof DetailedMapSection detailedMapSection) {
            BitmapUtil.setBitWithBitIndex(detailedMapSection.getBitmap(), bitX, bitY); // 기본 줌 사용
        }
    }

    @Deprecated
    public static void applyPositions(MapSection mapSection, List<Point> positions, double radiusMeters) {
        if (mapSection instanceof DetailedMapSection) {
            for (Point pos : positions) {
                applyPosition(mapSection, pos, radiusMeters);
            }
        }
    }
}
