package com.dudoji.spring.controller;

import com.dudoji.spring.dto.pin.PinResponseDto;
import com.dudoji.spring.dto.pin.PinRequestDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.PinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/user/pins")
public class PinController {

    @Autowired
    private PinService pinService;

    @PostMapping("")
    public ResponseEntity<PinResponseDto> savePin(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody PinRequestDto pinRequestDto) {
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
        List<PinResponseDto> pins = pinService.getClosePins(radius, lat, lng, principalDetails.getUid());
        pinService.refreshLikes(); // TODO: CHANGE
        return ResponseEntity.status(HttpStatus.OK).body(pins);
    }

    @PostMapping("/{pinId}/like")
    public ResponseEntity<Boolean> likePin(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable long pinId
    ) {
        boolean result = pinService.likePin(principalDetails.getUid(), pinId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{pinId}/like")
    public ResponseEntity<Boolean> unlikePin(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable long pinId
    ) {
        boolean result = pinService.unlikePin(principalDetails.getUid(), pinId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<PinResponseDto>> getMyPins(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        List<PinResponseDto> pins = pinService.getMyPins(principalDetails.getUid());
        return ResponseEntity.status(HttpStatus.OK).body(pins);
    }
}