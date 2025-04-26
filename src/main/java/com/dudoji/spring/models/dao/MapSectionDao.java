package com.dudoji.spring.models.dao;

import com.dudoji.spring.dto.MapSectionResponseDto;
import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.DetailedMapSection;
import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Pair;
import com.dudoji.spring.models.domain.Point;
import com.dudoji.spring.util.BitmapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
public class MapSectionDao {

    @Autowired
    private DBConnection dbConnection;

    private static final String GET_MAP_SECTIONS =
            "select m.x, m.y, mb.bitmap " +
            "    from MapSectionStateBitmap as mb right join (\n" +
            "    select uid, x, y, explored\n" +
            "    from MapSection\n" +
            "    where uid=? and (? <= x and x <= ? and ? <= y and y <= ?)\n" +
            "    ) as m\n" +
            "    on (m.uid=mb.uid and m.x=mb.x and m.y =mb.y);";

    private static final String GET_MAP_SECTION =
            "select m.x, m.y, mb.bitmap " +
                    "    from MapSectionStateBitmap as mb right join (\n" +
                    "    select uid, x, y, explored\n" +
                    "    from MapSection\n" +
                    "    where uid=? and (x=? and y=?)\n" +
                    "    ) as m\n" +
                    "    on (m.uid=mb.uid and m.x=mb.x and m.y =mb.y);";

    private static final String SET_MAP_SECTION =
            "INSERT INTO MapSection (uid, x, y) " +
                    "VALUES (?, ?, ?);";

    private static final String SET_MAP_SECTION_BITMAP =
            "INSERT INTO MapSectionStateBitmap (uid, x, y, bitmap) " +
                    "VALUES (?, ?, ?, ?);";

    private static final String UPDATE_MAP_SECTION_BITMAP =
            "UPDATE MapSectionStateBitmap " +
                    "SET bitmap=? " +
                    "WHERE uid=? and X=? and Y=?;";

    private static final String UPDATE_MAP_SECTION =
            "UPDATE MapSection " +
                    "SET explored=TRUE " +
                    "WHERE uid=? and X=? and Y=?;";

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
                    + "  SELECT uid, x, y, explored "
                    + "  FROM MapSection "
                    + "  WHERE uid = ? "
                    + ") AS m "
                    + "  ON mb.uid = m.uid "
                    + "  AND mb.x   = m.x "
                    + "  AND mb.y   = m.y;";

    public List<MapSection> getMapSections(long uid, Point point) {
        return getMapSections(uid, point, 2);
    }

    public List<MapSection> getMapSections(long uid, Point point, int radius){
        Pair<Double, Double> googleMapPosition = point.getGoogleMap();
        Pair<Integer, Integer> tileMapPosition = Point.convertGoogleMercatorToTile(googleMapPosition.getX(), point.getGoogleY());
        int maxX = tileMapPosition.getX() + radius;
        int maxY = tileMapPosition.getY()+ radius;
        int minX = tileMapPosition.getX() - radius;
        int minY = tileMapPosition.getY() - radius;

        try (Connection connection = dbConnection.getConnection()) {
            List<MapSection> mapSections = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_MAP_SECTIONS);
            preparedStatement.setLong(1, uid);
            preparedStatement.setInt(2, minX);
            preparedStatement.setInt(3, maxX);
            preparedStatement.setInt(4, minY);
            preparedStatement.setInt(5, maxY);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                int x = resultSet.getInt("m.x");
                int y = resultSet.getInt("m.y");
                byte[] bitmap = resultSet.getBytes("mb.bitmap");
                MapSection mapSection = new MapSection.Builder()
                        .setUid(uid)
                        .setX(x)
                        .setY(y)
                        .setBitmap(bitmap)
                        .build();
                mapSections.add(mapSection);
            }
            return mapSections;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<MapSection> getMapSection(long uid, Point point) {
        Pair<Double, Double> googleMapPosition = point.getGoogleMap();
        Pair<Integer, Integer> tileMapPosition = Point.convertGoogleMercatorToTile(googleMapPosition.getX(), point.getGoogleY());

        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_MAP_SECTION);
            preparedStatement.setLong(1, uid);
            preparedStatement.setInt(2, tileMapPosition.getX());
            preparedStatement.setInt(3, tileMapPosition.getY());
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                byte[] bitmap = resultSet.getBytes("bitmap");
                MapSection mapSection = new MapSection.Builder()
                        .setUid(uid)
                        .setX(x)
                        .setY(y)
                        .setBitmap(bitmap)
                        .build();
                return Optional.of(mapSection);
            }
            else {
                System.out.println("There is no MapSection");
                return Optional.empty();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public Optional<MapSection> getMapSection(long uid, int tileX, int tileY) {
        Pair<Integer, Integer> tileMapPosition = new Pair<>(tileX, tileY);

        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_MAP_SECTION);
            preparedStatement.setLong(1, uid);
            preparedStatement.setInt(2, tileMapPosition.getX());
            preparedStatement.setInt(3, tileMapPosition.getY());
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                byte[] bitmap = resultSet.getBytes("bitmap");
                MapSection mapSection = new MapSection.Builder()
                        .setUid(uid)
                        .setX(x)
                        .setY(y)
                        .setBitmap(bitmap)
                        .build();
                return Optional.of(mapSection);
            }
            else {
                System.out.println("There is no MapSection");
                return Optional.empty();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void createMapSection(MapSection mapSection) {
        if (mapSection instanceof DetailedMapSection detailedMapSection) {
            long uid = detailedMapSection.getUid();
            Pair<Integer, Integer> tileMapPosition = detailedMapSection.getPoint();

            try (Connection connection = dbConnection.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(SET_MAP_SECTION);
                preparedStatement.setLong(1, uid);
                preparedStatement.setInt(2, tileMapPosition.getX());
                preparedStatement.setInt(3, tileMapPosition.getY());

                preparedStatement.executeUpdate();

                PreparedStatement preparedStatement1 = connection.prepareStatement(SET_MAP_SECTION_BITMAP);
                preparedStatement1.setLong(1, uid);
                preparedStatement1.setInt(2, tileMapPosition.getX());
                preparedStatement1.setInt(3, tileMapPosition.getY());
                preparedStatement1.setBytes(4, ((DetailedMapSection) mapSection).getBitmap());

                preparedStatement1.executeUpdate();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateMapSection(MapSection mapSection) {
        if (mapSection instanceof DetailedMapSection detailedMapSection) {
            // detailed 아니면 업데이트 할 이유도 없음
            long uid = detailedMapSection.getUid();
            Pair<Integer, Integer> tileMapPosition = detailedMapSection.getPoint();
            byte[] bitmap = detailedMapSection.getBitmap();

            try (Connection connection = dbConnection.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MAP_SECTION_BITMAP);
                preparedStatement.setBytes(1, bitmap);
                preparedStatement.setLong(2, uid);
                preparedStatement.setInt(3, tileMapPosition.getX());
                preparedStatement.setInt(4, tileMapPosition.getY());
                preparedStatement.executeUpdate();

                if (BitmapUtil.isExplored(bitmap)) {
                    // TODO: 여기 들어오면 없애는 기능도 만드는 게 필요할 듯
                    PreparedStatement preparedStatement1 = connection.prepareStatement(UPDATE_MAP_SECTION);
                    preparedStatement1.setLong(1, uid);
                    preparedStatement1.setInt(2, tileMapPosition.getX());
                    preparedStatement1.setInt(3, tileMapPosition.getY());
                    preparedStatement1.executeUpdate();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    // Using In MapSectionController
    public MapSectionResponseDto getUserMapSections(long uid) {
        MapSectionResponseDto mapSectionResponseDto = new MapSectionResponseDto();

        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_MAP_SECTIONS);
            preparedStatement.setLong(1, uid);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                boolean explored = resultSet.getBoolean("explored");
                byte[] bitmap = resultSet.getBytes("bitmap");

                String bitmapBase64 = (bitmap != null && bitmap.length > 0) ? Base64.getEncoder().encodeToString(bitmap) : "";
                mapSectionResponseDto.mapSections.add(
                        MapSectionResponseDto.MapSectionDto.builder()
                                .x(x)
                                .y(y)
                                .explored(explored)
                                .base64Encoded(bitmapBase64)
                        .build()
                );
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return mapSectionResponseDto;
    }
}
