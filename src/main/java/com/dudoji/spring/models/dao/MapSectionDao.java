package com.dudoji.spring.models.dao;

import com.dudoji.spring.models.DBConnection;
import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            "    on (m.uid=mb.uid and m.x=mb.x and m.y =mb.y)\n" +
            ");";


    public List<MapSection> getMapSections(long uid, Point point) {
        return getMapSections(uid, point, 2);
    }

    public List<MapSection> getMapSections(long uid, Point point, int radius){
        int maxX = point.getX() + radius;
        int maxY = point.getY() + radius;
        int minX = point.getX() - radius;
        int minY = point.getY() - radius;
        try (Connection connection = dbConnection.getConnection()) {
            List<MapSection> mapSections = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_MAP_SECTIONS);
            preparedStatement.setLong(1, uid);
            preparedStatement.setInt(2, minX);
            preparedStatement.setInt(3, maxX);
            preparedStatement.setInt(4, minY);
            preparedStatement.setInt(5, maxY);
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

    public void createMapSections(List<MapSection> mapSections){
        // TODO
    }
}
