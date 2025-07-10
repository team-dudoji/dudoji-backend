package com.dudoji.spring.service.skin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dudoji.spring.dto.skin.CharacterSkinDto;
import com.dudoji.spring.dto.skin.CharacterSkinDto;
import com.dudoji.spring.models.dao.skin.CharacterSkinDao;
import com.dudoji.spring.models.domain.skin.CharacterSkin;

@Service
public class CharacterSkinService {

	@Autowired
	private CharacterSkinDao characterSkinDao;

	public List<CharacterSkinDto> getCharacterSkins(long userId) {
		return characterSkinDao.getCharacterSkins(userId);
	}

	public List<CharacterSkinDto> getPurchasedCharacterSkins(long userId) {
		return getCharacterSkins(userId)
			.stream()
			.filter(CharacterSkinDto::isPurchased)
			.toList();
	}

	public long upsertCharacterSkin(CharacterSkin characterSkin) {
		return characterSkinDao.createOrUpdateCharacterSkin(characterSkin);
	}

	public boolean deleteCharacterSkin(long skinId) {
		return characterSkinDao.deleteCharacterSkin(skinId);
	}

	public boolean updateUserCharacterSkin(long skinId, long userId) {
		return characterSkinDao.updateUserCharacterSkin(skinId, userId);
	}
}
