package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.models.dao.MapSectionDao;
import com.dudoji.spring.models.domain.DetailedMapSection;
import com.dudoji.spring.models.domain.MapSection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static com.dudoji.spring.models.domain.MapSection.TILE_SIZE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({MapSectionDao.class})
public class MapSectionDaoTest extends DBTestBase {

    @Autowired
    private MapSectionDao mapSectionDao;

    long userId = 101L;
    int tileX = 0;
    int tileY = 0;
    byte[] bitmap = new byte[TILE_SIZE];

    @Test
    void createAndFetch() {
        MapSection mapSection = new MapSection.Builder()
                .setUid(userId)
                .setX(tileX)
                .setY(tileY)
                .setBitmap(bitmap)
                .build();
        mapSectionDao.createMapSection(mapSection);

        Optional<MapSection> fetched = mapSectionDao.getMapSection(userId, tileX, tileY);

        assertThat(fetched).isPresent();
        assertThat(fetched.get()).isInstanceOf(MapSection.class);
        DetailedMapSection dms = (DetailedMapSection) fetched.get();
        assertEquals(dms.getUid(), userId);
        assertArrayEquals(bitmap, dms.getBitmap());
    }

    @Test
    void update() {
        MapSection mapSection = new MapSection.Builder()
                .setUid(userId)
                .setX(tileX)
                .setY(tileY)
                .setBitmap(bitmap)
                .build();
        mapSectionDao.createMapSection(mapSection);

        bitmap[0] = 7;
        MapSection newMapSection = new MapSection.Builder()
                .setUid(userId)
                .setX(tileX)
                .setY(tileY)
                .setBitmap(bitmap)
                .build();

        mapSectionDao.updateMapSection(newMapSection);

        Optional<MapSection> fetched = mapSectionDao.getMapSection(userId, tileX, tileY);

        assertThat(fetched).isPresent();
        assertThat(fetched.get()).isInstanceOf(MapSection.class);
        DetailedMapSection dms = (DetailedMapSection) fetched.get();
        assertEquals(dms.getUid(), userId);
        assertArrayEquals(bitmap, dms.getBitmap());
    }
}
