package com.dudoji.spring.controller;

import com.dudoji.spring.dto.pin.PinResponseDto;
import com.dudoji.spring.dto.pin.PinRequestDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.PinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/pins")
public class PinController {

    @Autowired
    private PinService pinService;

    @PostMapping("")
    public ResponseEntity<PinResponseDto> savePin(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody PinRequestDto pinRequestDto) {
        if (principalDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        PinResponseDto pinResponseDto = pinService.createPin(pinRequestDto.toDomain(principalDetails.getUid()));
        return ResponseEntity.status(HttpStatus.CREATED).body(pinResponseDto);
    }

    @GetMapping("")
    public ResponseEntity<List<PinResponseDto>> getPinsByRadius(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam double radius, // TODO: PARAM? OR PATH VARIABLE?
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<PinResponseDto> pins = pinService.getClosePins(radius, lat, lng, principalDetails.getUid());
        pinService.refreshLikes(); // TODO: CHANGE
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
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<PinResponseDto>> getMyPins(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<PinResponseDto> pins = pinService.getMyPins(principalDetails.getUid());
        return ResponseEntity.status(HttpStatus.OK).body(pins);
    }
}