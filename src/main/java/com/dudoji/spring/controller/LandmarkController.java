package com.dudoji.spring.controller;

import com.dudoji.spring.dto.landmark.LandmarkRequestDto;
import com.dudoji.spring.dto.landmark.LandmarkResponseDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.LandmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LandmarkController {
    private final LandmarkService landmarkService;

    @ResponseBody
    @GetMapping("/api/user/landmarks")
    public ResponseEntity<List<LandmarkResponseDto>> getLandmarks(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        return ResponseEntity.ok(
                landmarkService.getLandmarks(principalDetails.getUid())
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
    @GetMapping("/admin/landmarks")
    public String getAdminLandmarkPage(
            Model model
    ) {
        model.addAttribute("landmarks",
                landmarkService.getLandmarks(-1)
        );
        return "admin_landmarks";
    }

}
