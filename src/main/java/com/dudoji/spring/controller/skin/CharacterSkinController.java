package com.dudoji.spring.controller.skin;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dudoji.spring.dto.skin.CharacterSkinDto;
import com.dudoji.spring.dto.skin.CharacterSkinSimpleDto;
import com.dudoji.spring.dto.skin.PinSkinSimpleDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.skin.CharacterSkinService;

import lombok.RequiredArgsConstructor;

@Controller
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class CharacterSkinController {
	private final CharacterSkinService characterSkinService;
	@Value("${file.upload-dir}")
	private String uploadDir;

	@GetMapping("/api/user/character-skins")
	public ResponseEntity<List<CharacterSkinDto>> getCharacterSkins(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		List<CharacterSkinDto> result = characterSkinService.getCharacterSkins(principalDetails.getUid());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/api/user/character-skins/mine")
	public ResponseEntity<List<CharacterSkinDto>> getMineCharacterSkins(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		List<CharacterSkinDto> result = characterSkinService.getPurchasedCharacterSkins(principalDetails.getUid());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("/api/user/character-skins/{skinId}")
	public ResponseEntity<Boolean> updateUserCharacterSkins (
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@PathVariable Long skinId
	) {
		// TODO: 구매 계산 로직 여기서 할 것인지?
		boolean result = characterSkinService.updateUserCharacterSkin(skinId, principalDetails.getUid());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('admin')")
	@ResponseBody
	@PostMapping("/api/admin/character-skins")
	public ResponseEntity<Long> addCharacterSkin(
		@RequestBody CharacterSkinSimpleDto dto
	) {
		long result = characterSkinService.upsertCharacterSkin(dto.toDomain());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('admin')")
	@DeleteMapping("/api/admin/character-skins/{skinId}")
	public ResponseEntity<Boolean> deleteCharacterSkin(
		@PathVariable Long skinId
	) {
		boolean result = characterSkinService.deleteCharacterSkin(skinId);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('admin')")
	@GetMapping("/admin/character-skins")
	public String getAdminCharacterSkinPage(
		Model model
	) {
		model.addAttribute("characterSkins",
			characterSkinService.getCharacterSkins(-1)
		);
		model.addAttribute("uploadDir", uploadDir);
		return "admin_characterskins";
	}
}
