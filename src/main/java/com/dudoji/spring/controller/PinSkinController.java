package com.dudoji.spring.controller;

import com.dudoji.spring.dto.PinSkinCreateReq;
import com.dudoji.spring.dto.PinSkinDto;
import com.dudoji.spring.models.domain.PinSkin;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.PinSkinService;
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
import java.util.stream.Collectors;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

@Controller
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
        if (principalDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<PinSkinDto> result = pinSkinService.getPinSkins(principalDetails.getUid());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/api/user/pin-skins/mine")
    public ResponseEntity<List<PinSkinDto>> getMinePinSkins(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<PinSkinDto> result = pinSkinService.getPinSkins(principalDetails.getUid())
                .stream()
                .filter(PinSkinDto::isPurchased)
                .toList();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/api/user/pin-skins/{skinId}")
    public ResponseEntity<Boolean> updateUserPinSkins(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long skinId
    ) {
        if (principalDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // TODO: 구매 계산 로직
        boolean result = pinSkinService.updateUserPinSkin(skinId ,principalDetails.getUid());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('admin')")
    @ResponseBody
    @PostMapping("/api/admin/pin-skins")
    public ResponseEntity<Long> addPinSkin(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody PinSkinCreateReq req
    ) {
        if (principalDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        PinSkin entity = PinSkin.builder()
                                .name(req.name())
                                .content(req.content())
                                .imageUrl(req.imageUrl())
                                .price(req.price())
                                .build();
        long result = pinSkinService.upsertPinSkin(entity);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/api/admin/pin-skins/{skinId}")
    public ResponseEntity<Boolean> deletePinSkin(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long skinId
    ) {
        if (principalDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

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
        return "admin_pinSkins";
    }
}
