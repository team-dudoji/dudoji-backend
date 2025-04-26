package com.dudoji.spring.service;

import com.dudoji.spring.dto.MapSectionResponseDto;
import com.dudoji.spring.models.dao.MapSectionDao;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.DetailedMapSection;
import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Pair;
import com.dudoji.spring.models.domain.Point;
import com.dudoji.spring.util.BitmapUtil;
import com.dudoji.spring.util.MapSectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.dudoji.spring.models.domain.MapSection.BASIC_ZOOM_SIZE;
import static com.dudoji.spring.models.domain.MapSection.TILE_SIZE;

@Service
public class MapSectionService {
    public static final int BYTE_SIZE = 8;
    @Autowired
    private MapSectionDao mapSectionDao;
    @Autowired
    private UserDao userDao;

    public void applyRevealCircle(long uid, Point centerPoint, double radiusMeters) {
        Pair<Double, Double> googleMapPosition = centerPoint.getGoogleMap();
        Pair<Integer, Integer> tilePosition = Point.convertGoogleMercatorToTile(
                googleMapPosition.getX(), googleMapPosition.getY()
        );
        int tileX = tilePosition.getX();
        int tileY = tilePosition.getY();

        // 중심 맵섹션 색칠하기
        Optional<MapSection> centerMapSection = mapSectionDao.getMapSection(uid, centerPoint);
        MapSection mapSection = centerMapSection.orElseGet(() -> setMapSectionByGeographic(uid, centerPoint));
        MapSectionUtil.applyPosition(mapSection, centerPoint, radiusMeters);
        mapSectionDao.updateMapSection(mapSection);

        // 주변 침공하는 친구들 전부 설정
        Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> candidateMap =
                getCandidateTiles(tileX, tileY, centerPoint, radiusMeters);
        for (Pair<Integer, Integer> tileKey : candidateMap.keySet()) {
            Optional<MapSection> targetMapSection = mapSectionDao.getMapSection(uid, tileKey.getX(), tileKey.getY());
            MapSection tempMapSection = targetMapSection.orElseGet(() -> setMapSectionByTile(uid, tileKey.getX(), tileKey.getY()));
            List<Pair<Integer, Integer>> positionList = candidateMap.get(tileKey);
            for (Pair<Integer, Integer> pos : positionList) {
                MapSectionUtil.applyPostionWithBit(tempMapSection, pos.getX(), pos.getY());
            }
            mapSectionDao.updateMapSection(tempMapSection);
        }
    }

    private Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> getCandidateTiles(int tileX, int tileY, Point centerPoint, double radiusMeters, int zoom) {
        Pair<Double, Double> centerGoogle = centerPoint.getGoogleMap();
        Pair<Integer, Integer> centerPixel = Point.convertGoogleMercatorToPixel(centerGoogle.getX(), centerGoogle.getY());

        int centerX = centerPixel.getX() % TILE_SIZE;
        int centerY = centerPixel.getY() % TILE_SIZE;

        double pixelRadius = BitmapUtil.getPixelRadius(centerPoint, zoom, radiusMeters);

        int minX = (int)Math.floor(centerX - pixelRadius);
        int maxX = (int)Math.ceil(centerX + pixelRadius);
        int minY = (int)Math.floor(centerY - pixelRadius);
        int maxY = (int)Math.ceil(centerY + pixelRadius);

        Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> result = new HashMap<>();
        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                int dx = px - centerX;
                int dy = py - centerY;
                int distSq = dx*dx + dy*dy;
                if (distSq > pixelRadius * pixelRadius) continue; // 원 밖일 때

                int offsetX = 0;
                int offsetY = 0;

                int localX = px;
                int localY = py;

                while (localX < 0) {
                    localX += TILE_SIZE;
                    offsetX -= 1;
                }
                while (localX >= TILE_SIZE) {
                    localX -= TILE_SIZE;
                    offsetX += 1;
                }
                while (localY < 0) {
                    localY += TILE_SIZE;
                    offsetY -= 1;
                }
                while (localY >= TILE_SIZE) {
                    localY -= TILE_SIZE;
                    offsetY += 1;
                }

                int candidateTileX = tileX + offsetX;
                int candidateTileY = tileY + offsetY;
                Pair<Integer, Integer> tileKey = new Pair<>(candidateTileX, candidateTileY);

                result.computeIfAbsent(tileKey, k -> new ArrayList<>())
                        .add(new Pair<>(localX, localY));
            }
        }

        return result;
    }

    private Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> getCandidateTiles(int tileX, int tileY, Point centerPoint, double radiusMeters) {
        return getCandidateTiles(tileX, tileY, centerPoint, radiusMeters, BASIC_ZOOM_SIZE);
    }

    /**
     * Get MapSection Object from DataBase with Using Optional Value.
     * @param uid User ID in Database
     * @param point Point which be targeted
     * @return Optional MapSection, If It is not DetailedMapSection Object return empty.
     */
    public Optional<MapSection> getMapSectionByGeographic(long uid, Point point) {
        MapSectionDao mapSectionDao = new MapSectionDao();
        Optional<MapSection> mapSection = mapSectionDao.getMapSection(uid, point);

        if (mapSection.isEmpty()) {
            // mapSection 이 DB 에 존재하지 않을 때
            return Optional.of(setMapSectionByGeographic(uid, point));
        }
        else {
            if (mapSection.get() instanceof DetailedMapSection) return mapSection;
            else return Optional.empty();
        }
    }

    public MapSection setMapSectionByGeographic(long uid, Point point) {
        Pair<Double, Double> googleMapPosition = point.getGoogleMap();
        Pair<Integer, Integer> tilePosition = Point.convertGoogleMercatorToTile(googleMapPosition.getX(), googleMapPosition.getY());

        MapSection mapSection = new MapSection.Builder()
                .setUid(uid)
                .setX(tilePosition.getX())
                .setY(tilePosition.getY())
                .setBitmap(new byte[TILE_SIZE * TILE_SIZE / BYTE_SIZE])
                .build();
        mapSectionDao.createMapSection(mapSection);
        return mapSection;
    }

    public MapSection setMapSectionByTile(long uid, int tileX, int tileY) {
        Pair<Integer, Integer> tilePosition = new Pair<>(tileX, tileY);

        MapSection mapSection = new MapSection.Builder()
                .setUid(uid)
                .setX(tilePosition.getX())
                .setY(tilePosition.getY())
                .setBitmap(new byte[TILE_SIZE * TILE_SIZE / BYTE_SIZE])
                .build();
        mapSectionDao.createMapSection(mapSection);
        return mapSection;

    }

    // Using MapSectionController
    public MapSectionResponseDto getUserMapSections(long uid) {
        return mapSectionDao.getUserMapSections(uid);
    }
}
