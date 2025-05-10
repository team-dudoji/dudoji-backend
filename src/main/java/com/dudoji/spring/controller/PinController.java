package com.dudoji.spring.controller;

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

    @PostMapping("/save")
    public ResponseEntity<String> savePin(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody PinRequestDto pinRequestDto) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        pinService.createPin(pinRequestDto.toDomain(principalDetails.getUid()));
        return ResponseEntity.status(HttpStatus.CREATED).body("PIN created");
    }

    @GetMapping("/get/pins-by-radius")
    public ResponseEntity<List<Pin>> getPinsByRadius(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam double radius,
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<Pin> pins = pinService.getClosePins(radius, lat, lng);
        return ResponseEntity.status(HttpStatus.OK).body(pins);
    }
}