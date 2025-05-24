package com.dudoji.spring.controller;

import com.dudoji.spring.dto.PinDto;
import com.dudoji.spring.dto.PinRequestDto;
import com.dudoji.spring.models.domain.Pin;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.PinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/pin")
public class PinController {

    @Autowired
    private PinService pinService;

    @PostMapping("/")
    public ResponseEntity<String> savePin(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody PinRequestDto pinRequestDto) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        pinService.createPin(pinRequestDto.toDomain(principalDetails.getUid()));
        return ResponseEntity.status(HttpStatus.CREATED).body("PIN created");
    }

    @GetMapping("/")
    public ResponseEntity<List<PinDto>> getPinsByRadius(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam double radius, // TODO: PARAM? OR PATH VARIABLE?
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<PinDto> pins = pinService.getClosePins(radius, lat, lng, principalDetails.getUid());
        return ResponseEntity.status(HttpStatus.OK).body(pins);
    }

    @PostMapping("/{pinId}/like")
    public ResponseEntity<Boolean> likePin(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable long pinId
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        boolean result = pinService.likePin(principalDetails.getUid(), pinId);
        pinService.refreshLikes(); // TODO: CHANGE
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{pinId}/like")
    public ResponseEntity<Boolean> unlikePin(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable long pinId
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        boolean result = pinService.unlikePin(principalDetails.getUid(), pinId);
        pinService.refreshLikes(); // TODO: CHANGE
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}