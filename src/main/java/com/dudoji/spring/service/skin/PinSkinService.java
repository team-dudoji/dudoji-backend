package com.dudoji.spring.service.skin;

import com.dudoji.spring.dto.skin.PinSkinDto;
import com.dudoji.spring.models.dao.skin.PinSkinDao;
import com.dudoji.spring.models.domain.skin.PinSkin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PinSkinService {
    @Autowired
    private PinSkinDao pinSkinDao;

    public List<PinSkinDto> getPinSkins(long userId) {
        return pinSkinDao.getPinSkins(userId);
    }

    public List<PinSkinDto> getPurchasedPinSkins(long userId) {
        return getPinSkins(userId)
                .stream()
                .filter(PinSkinDto::isPurchased)
                .toList();
    }

    public long upsertPinSkin(PinSkin pinSkin) {
        return pinSkinDao.createOrUpdatePinSkin(pinSkin);
    }

    public boolean deletePinSkin(long skinId) {
        return pinSkinDao.deletePinSkin(skinId);
    }

    public boolean updateUserPinSkin(long skinId, long userId) {
        return pinSkinDao.updateUserPinSkin(skinId, userId);
    }
}
