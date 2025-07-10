package com.dudoji.spring.controller.skin;

import com.dudoji.spring.dto.skin.PinSkinSimpleDto;
import com.dudoji.spring.dto.skin.PinSkinDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.skin.PinSkinService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class PinSkinController {

    @Autowired
    private PinSkinService pinSkinService;

    @Value("${file.upload-dir}") String uploadDir;

    // Get all PinSkin contain whether purchase
    @GetMapping("/api/user/pin-skins")
    public ResponseEntity<List<PinSkinDto>> getPinSkins(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        List<PinSkinDto> result = pinSkinService.getPinSkins(principalDetails.getUid());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/api/user/pin-skins/mine")
    public ResponseEntity<List<PinSkinDto>> getMinePinSkins(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        List<PinSkinDto> result = pinSkinService.getPurchasedPinSkins(principalDetails.getUid());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/api/user/pin-skins/{skinId}")
    public ResponseEntity<Boolean> updateUserPinSkins(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long skinId
    ) {
        // TODO: 구매 계산 로직
        boolean result = pinSkinService.updateUserPinSkin(skinId, principalDetails.getUid());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('admin')")
    @ResponseBody
    @PostMapping("/api/admin/pin-skins")
    public ResponseEntity<Long> addPinSkin(
            @RequestBody PinSkinSimpleDto dto
    ) {
        long result = pinSkinService.upsertPinSkin(dto.toDomain());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/api/admin/pin-skins/{skinId}")
    public ResponseEntity<Boolean> deletePinSkin(
            @PathVariable Long skinId
    ) {
        boolean result = pinSkinService.deletePinSkin(skinId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/admin/pin-skins")
    public String getAdminPinSkinPage(
            Model model
    ) {
        model.addAttribute("pinSkins",
                pinSkinService.getPinSkins(-1)
        );
        model.addAttribute("uploadDir", uploadDir);
        return "admin_pinskins";
    }
}
