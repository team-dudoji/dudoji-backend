package com.dudoji.spring.service;

import com.dudoji.spring.dto.PinSkinDto;
import com.dudoji.spring.models.dao.PinSkinDao;
import com.dudoji.spring.models.domain.PinSkin;
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
