package com.dudoji.spring.util;

import com.dudoji.spring.models.domain.DetailedMapSection;
import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Pair;
import com.dudoji.spring.models.domain.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.dudoji.spring.util.BitmapUtil.*;

public class MapSectionUtil {
    // TODO - apply user positions to MapSection

    public static MapSection applyPosition(MapSection mapSection, int lng, int lat) {
        // GPS 받아온 위도 경도를 MapSection에 적용한다. (Bitmap Util을 활용)
        // return은 적용한 MapSection
        if (!(mapSection instanceof DetailedMapSection detailed)) {
            // 작업할 비트맵이 없기에
            // 또는 다른 로직 추가해서 Detailed로 변경
            return mapSection;
        }

        Point basePoint = detailed.getPoint();
        Point targetPoint = Point.fromGeographic(lng, lat);

        setBit(detailed.getBitmap(), basePoint, targetPoint);
        return mapSection;
    }

    public static MapSection applyPositions(MapSection mapSection, List<Pair<Integer, Integer>> positions) {
        for (Pair<Integer, Integer> position : positions) {
            applyPosition(mapSection, position.getX(), position.getY());
        }
        return mapSection;
    }

    public static List<Point> getMapSectionPointsByPosition(int lng, int lat, int radius) {
        // TODO
        // 위도 경도, 범위를 받아와서 거기에 겹치는 MapSections들의 좌표를 반환한다.
        // 그럼 겹쳤다는 신호만 주면 ㄹㅇ 연산 두어번 더 해야되는 거잖아
        // 근데 또 get Point 이기 때문에 겹친 곳 리스트만 넘겨주는 식으로 구현 해도 될 듯 말 듯
        // 이거 오차 많이 날 듯
        Set<Point> result = new HashSet<>();

        int radiusBit = radius / GRID_SIZE;
        int radiusBitSq = radiusBit * radiusBit;

        Point targetPoint = Point.fromGeographic(lng, lat);
        Pair<Integer, Integer> dudojiPoint = targetPoint.getDudoji();
        Pair<Integer, Integer> targetUTM = targetPoint.getUtmk();

        Point basePoint = Point.fromDUDOJI(dudojiPoint.getX(), dudojiPoint.getY());
        Pair<Integer, Integer> baseUTM = basePoint.getUtmk();

        int xIndex = Math.floorDiv(targetUTM.getX() - baseUTM.getX(), GRID_SIZE);
        int yIndex = Math.floorDiv(targetUTM.getY() - baseUTM.getY(), GRID_SIZE);

        int startX = xIndex - radiusBit;
        int endX = xIndex + radiusBit;
        int startY = yIndex - radiusBit;
        int endY = yIndex + radiusBit;

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int dx = x - xIndex;
                int dy = y - yIndex;
                // 원 범위에 걸리고
                if (dx*dx + dy*dy <= radiusBitSq &&
                        (x < 0 || x >= MAP_SECTION_SIZE || y < 0 || y > MAP_SECTION_SIZE)
                ) {
                    // 중복의 문제가 있음.
                    result.add(Point.fromDUDOJI(Math.floorDiv(x, 128), Math.floorDiv(y, 128)));
                }
            }
        }
        return new ArrayList<>(result);
    }
}
