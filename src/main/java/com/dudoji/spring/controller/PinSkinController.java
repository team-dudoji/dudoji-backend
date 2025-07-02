package com.dudoji.spring.controller;

import com.dudoji.spring.dto.PinSkinCreateReq;
import com.dudoji.spring.dto.PinSkinDto;
import com.dudoji.spring.models.domain.PinSkin;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.PinSkinService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

@RestController
@RequiredArgsConstructor
public class PinSkinController {

    @Autowired
    private PinSkinService pinSkinService;

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

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
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
}
