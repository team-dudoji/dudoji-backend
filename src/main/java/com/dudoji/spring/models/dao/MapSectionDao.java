package com.dudoji.spring.models.dao;

import com.dudoji.spring.dto.mapsection.MapSectionResponseDto;
import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.DetailedMapSection;
import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Pair;
import com.dudoji.spring.models.domain.Point;
import com.dudoji.spring.util.BitmapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class MapSectionDao {

    @Autowired
    private JdbcClient jdbcClient;

    private static final String GET_MAP_SECTIONS =
            "select m.x, m.y, mb.bitmap " +
            "    from MapSectionStateBitmap as mb right join (\n" +
            "    select userId, x, y, explored\n" +
            "    from MapSection\n" +
            "    where userId=? and (? <= x and x <= ? and ? <= y and y <= ?)\n" +
            "    ) as m\n" +
            "    on (m.userId=mb.userId and m.x=mb.x and m.y =mb.y);";

    private static final String GET_MAP_SECTION =
            "select m.x, m.y, mb.bitmap " +
                    "    from MapSectionStateBitmap as mb right join (\n" +
                    "    select userId, x, y, explored\n" +
                    "    from MapSection\n" +
                    "    where userId=? and (x=? and y=?)\n" +
                    "    ) as m\n" +
                    "    on (m.userId=mb.userId and m.x=mb.x and m.y =mb.y);";

    private static final String SET_MAP_SECTION =
            "INSERT INTO MapSection (userId, x, y) " +
                    "VALUES (?, ?, ?);";

    private static final String SET_MAP_SECTION_BITMAP =
            "INSERT INTO MapSectionStateBitmap (userId, x, y, bitmap) " +
                    "VALUES (?, ?, ?, ?);";

    private static final String UPDATE_MAP_SECTION_BITMAP =
            "UPDATE MapSectionStateBitmap " +
                    "SET bitmap=? " +
                    "WHERE userId=? and X=? and Y=?;";

    private static final String UPDATE_MAP_SECTION =
            "UPDATE MapSection " +
                    "SET explored=TRUE " +
                    "WHERE userId=? and X=? and Y=?;";

    // Using in MapSectionController
    private static final String GET_USER_MAP_SECTIONS =
            ""
                    + "SELECT m.x, m.y, m.explored, "
                    + "CASE "
                    + "  WHEN m.explored = TRUE THEN null "
                    + "  ELSE mb.bitmap "
                    + "END AS bitmap "
                    + "FROM MapSectionStateBitmap AS mb "
                    + "RIGHT JOIN ( "
                    + "  SELECT userId, x, y, explored "
                    + "  FROM MapSection "
                    + "  WHERE userId = ? "
                    + ") AS m "
                    + "  ON mb.userId = m.userId "
                    + "  AND mb.x   = m.x "
                    + "  AND mb.y   = m.y;";

    public List<MapSection> getMapSections(long userId, Point point) {
        return getMapSections(userId, point, 2);
    }

    public List<MapSection> getMapSections(long userId, Point point, int radius){
        Pair<Double, Double> googleMapPosition = point.getGoogleMap();
        Pair<Integer, Integer> tileMapPosition = Point.convertGoogleMercatorToTile(googleMapPosition.getX(), point.getGoogleY());
        int maxX = tileMapPosition.getX() + radius;
        int maxY = tileMapPosition.getY()+ radius;
        int minX = tileMapPosition.getX() - radius;
        int minY = tileMapPosition.getY() - radius;

        return jdbcClient.sql(GET_MAP_SECTIONS)
                .param(userId)
                .param(minX)
                .param(maxX)
                .param(minY)
                .param(maxY)
                .query((rs, rowNum) ->
                        new MapSection.Builder()
                                .setUid(userId)
                                .setX(rs.getInt("x"))
                                .setY(rs.getInt("y"))
                                .setBitmap(rs.getBytes("bitmap"))
                                .build())
                .list();
    }

    public Optional<MapSection> getMapSection(long userId, Point point) {
        Pair<Double, Double> googleMapPosition = point.getGoogleMap();
        Pair<Integer, Integer> tileMapPosition = Point.convertGoogleMercatorToTile(googleMapPosition.getX(), point.getGoogleY());


        Optional<MapSection> section =
                jdbcClient.sql(GET_MAP_SECTION)
                        .param(userId)
                        .param(tileMapPosition.getX())
                        .param(tileMapPosition.getY())
                        .query((rs, rowNum) ->
                                new MapSection.Builder()
                                        .setUid(userId)
                                        .setX(rs.getInt("x"))
                                        .setY(rs.getInt("y"))
                                        .setBitmap(rs.getBytes("bitmap"))
                                        .build())
                        .optional();

        if (section.isEmpty()) {
            log.error("There is no such map section");
        }
        return section;
    }
    public Optional<MapSection> getMapSection(long userId, int tileX, int tileY) {
        Pair<Integer, Integer> tileMapPosition = new Pair<>(tileX, tileY);

        Optional<MapSection> section =
                jdbcClient.sql(GET_MAP_SECTION)
                        .param(userId)
                        .param(tileX)
                        .param(tileY)
                        .query((rs, rowNum) ->
                                new MapSection.Builder()
                                        .setUid(userId)
                                        .setX(rs.getInt("x"))
                                        .setY(rs.getInt("y"))
                                        .setBitmap(rs.getBytes("bitmap"))
                                        .build())
                        .optional();

        if (section.isEmpty()) {
            log.error("There is no such map section");
        }
        return section;
    }

    @Transactional
    public void createMapSection(MapSection mapSection) {
        if (mapSection instanceof DetailedMapSection detailedMapSection) {
            long userId = detailedMapSection.getUid();
            Pair<Integer, Integer> tileMapPosition = detailedMapSection.getPoint();

            jdbcClient.sql(SET_MAP_SECTION)
                    .param(userId)
                    .param(tileMapPosition.getX())
                    .param(tileMapPosition.getY())
                    .update();
            jdbcClient.sql(SET_MAP_SECTION_BITMAP)
                    .param(userId)
                    .param(tileMapPosition.getX())
                    .param(tileMapPosition.getY())
                    .param(detailedMapSection.getBitmap())
                    .update();
        }
    }

    public void updateMapSection(MapSection mapSection) {
        if (mapSection instanceof DetailedMapSection detailedMapSection) {
            // detailed 아니면 업데이트 할 이유도 없음
            long userId = detailedMapSection.getUid();
            Pair<Integer, Integer> tileMapPosition = detailedMapSection.getPoint();
            byte[] bitmap = detailedMapSection.getBitmap();

            jdbcClient.sql(UPDATE_MAP_SECTION_BITMAP)
                    .param(bitmap)
                    .param(userId)
                    .param(tileMapPosition.getX())
                    .param(tileMapPosition.getY())
                    .update();

            if (BitmapUtil.isExplored(bitmap)) {
                // TODO: 진입 시 맵섹션 없애는 로직
                jdbcClient.sql(UPDATE_MAP_SECTION)
                        .param(userId)
                        .param(tileMapPosition.getX())
                        .param(tileMapPosition.getY())
                        .update();
            }
        }
    }

    // Using In MapSectionController
    public MapSectionResponseDto getUserMapSections(long userId) {
        MapSectionResponseDto dto = new MapSectionResponseDto();

        List<MapSectionResponseDto.MapSectionDto> sections =
                jdbcClient.sql(GET_USER_MAP_SECTIONS)
                        .param(userId)
                        .query((rs, rowNum) -> {
                            byte[] bitmap = rs.getBytes("bitmap");
                            String base64 = (bitmap != null && bitmap.length > 0)
                                    ? Base64.getEncoder().encodeToString(bitmap)
                                    : "";
                            return MapSectionResponseDto.MapSectionDto.builder()
                                    .x(rs.getInt("x"))
                                    .y(rs.getInt("y"))
                                    .explored(rs.getBoolean("explored"))
                                    .base64Encoded(base64)
                                    .build();
                        })
                        .list();

        dto.mapSections.addAll(sections);
        return dto;
    }
}
