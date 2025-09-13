package com.dudoji.spring.controller;

import com.dudoji.spring.dto.landmark.LandmarkRequestDto;
import com.dudoji.spring.dto.landmark.LandmarkResponseDto;
import com.dudoji.spring.dto.mapsection.RevealCirclesRequestDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.LandmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class LandmarkController {
    private final LandmarkService landmarkService;

    @ResponseBody
    @GetMapping("/api/user/landmarks")
    public ResponseEntity<List<LandmarkResponseDto>> getLandmarks(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        return ResponseEntity.ok(
                landmarkService.getLandmarks(1)
        );
    }

    ///  Only need lat, lng, radius
    @GetMapping("/api/user/landmarks/point")
    public ResponseEntity<List<LandmarkResponseDto>> getLandmarkPoints(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody RevealCirclesRequestDto.RevealCircleDto revealCircleDto
    ) {
        return ResponseEntity.ok(
            landmarkService.getLandmarksCircleRadius(principalDetails.getUid(), revealCircleDto)
        );
    }

    @PreAuthorize("hasRole('admin')")
    @ResponseBody
    @PostMapping("/api/admin/landmarks")
    public ResponseEntity<String> addLandmark(
            @RequestBody LandmarkRequestDto landmarkRequestDto
            ) {
        landmarkService.saveLandmark(landmarkRequestDto);
        return ResponseEntity.ok("successfully saved");
    }

    @PreAuthorize("hasRole('admin')")
    @ResponseBody
    @DeleteMapping("/api/admin/landmarks/{landmarkId}")
    public ResponseEntity<String> deleteLandmark(
            @PathVariable Long landmarkId
    ) {
        landmarkService.deleteLandmark(landmarkId);
        return ResponseEntity.ok("successfully saved");
    }

    @PreAuthorize("hasRole('admin')")
    @ResponseBody
    @PutMapping("/api/admin/landmarks/{landmarkId}")
    public ResponseEntity<String> putLandmark(
            @PathVariable Long landmarkId,
            @RequestBody LandmarkRequestDto landmarkRequestDto
    ) {
        landmarkService.putLandmark(landmarkId, landmarkRequestDto);
        return ResponseEntity.ok("successfully saved");
    }

    @GetMapping("/api/user/landmarks/search")
    public ResponseEntity<List<LandmarkResponseDto>> getLandmarksByKeyword(
        @RequestParam String keyword
    ) {
        return ResponseEntity.ok(landmarkService.getLandmarksByKeyword(keyword));
    }
}
