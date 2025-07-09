package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.models.dao.skin.PinSkinDao;
import com.dudoji.spring.models.domain.skin.PinSkin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({PinSkinDao.class})
public class PinSkinDaoTest extends DBTestBase {

    @Autowired
    PinSkinDao pinSkinDao;

    long skinId = 3L;
    String skinName = "test";
    String skinContent = "test skin";
    String skinImageUrl = "test skin image";
    int skinPrice = 10;

    PinSkin newPinSkin = PinSkin.builder()
            .skinId(skinId)
            .name(skinName)
            .content(skinContent)
            .imageUrl(skinImageUrl)
            .price(skinPrice)
            .build();
    @Test
    void createAndUpdatePinSkin() {
        long dbSkinId = pinSkinDao.createOrUpdatePinSkin(newPinSkin);
        assertThat(dbSkinId).isGreaterThan(0);

        PinSkin pinSkinFromDB = pinSkinDao.findById(dbSkinId);
        assertThat(pinSkinFromDB.getName()).isEqualTo(skinName);

        String newContent = "new content";
        newPinSkin.setContent(newContent);

        long newDBSkinId = pinSkinDao.createOrUpdatePinSkin(newPinSkin);
        assertThat(newDBSkinId).isGreaterThan(0);
        assertEquals(newDBSkinId, dbSkinId);

        pinSkinFromDB = pinSkinDao.findById(newDBSkinId);
        assertThat(pinSkinFromDB.getContent()).isEqualTo(newContent);
    }

    @Test
    void deletePinSkin() {
        long dbSkinId = pinSkinDao.createOrUpdatePinSkin(newPinSkin);
        assertThat(dbSkinId).isGreaterThan(0);

        assertTrue(pinSkinDao.deletePinSkin(dbSkinId));
        assertFalse(pinSkinDao.deletePinSkin(dbSkinId));
        assertNull(pinSkinDao.findById(dbSkinId));
    }

    @Test
    void updateUserPinSkins() {
        long dbSkinId = pinSkinDao.createOrUpdatePinSkin(newPinSkin);
        assertThat(dbSkinId).isGreaterThan(0);
        assertTrue(pinSkinDao.updateUserPinSkin(dbSkinId, 101));
    }
}
