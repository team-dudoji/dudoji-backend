package com.dudoji.spring.service.skin;

import com.dudoji.spring.dto.skin.PinSkinDto;
import com.dudoji.spring.models.dao.skin.PinSkinDao;
import com.dudoji.spring.models.domain.skin.PinSkin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PinSkinService {
    @Autowired
    private PinSkinDao pinSkinDao;

    public List<PinSkinDto> getPinSkins(long userId, Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = Math.max(0, pageable.getPageNumber()) * Math.max(1, limit);
        Sort sort = pageable.getSort();

        return pinSkinDao.getPinSkins(userId, offset, limit, sort);
    }

    public Page<PinSkinDto> getPinSkinsPage(long userId, Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = Math.max(0, pageable.getPageNumber()) * Math.max(1, limit);
        Sort sort = pageable.getSort();

        return pinSkinDao.getPinSkinsPage(userId, offset, limit, sort);
    }

    public List<PinSkinDto> getPurchasedPinSkins(long userId, Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = Math.max(0, pageable.getPageNumber()) * Math.max(1, limit);
        Sort sort = pageable.getSort();

        return pinSkinDao.getPurchasedPinSkins(userId, offset, limit, sort);
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

    public PinSkinDto getPinSkinById(long userId, long skinId) {
        return pinSkinDao.getOnePinSkin(userId, skinId);
    }
}
