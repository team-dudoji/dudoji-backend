package com.dudoji.spring.controller;

import com.dudoji.spring.dto.landmark.LandmarkRequestDto;
import com.dudoji.spring.dto.landmark.LandmarkResponseDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.LandmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @ResponseBody
    @PostMapping("/api/admin/landmarks")
    public ResponseEntity<String> addLandmark(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody LandmarkRequestDto landmarkRequestDto
            ) {


        if (principalDetails == null) {
            // TODO - admin auth 해야함
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        landmarkService.saveLandmark(landmarkRequestDto);
        return ResponseEntity.ok("successfully saved");
    }

    @GetMapping("/admin/landmarks")
    public String getAdminLandmarkPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Model model
    ) {
        if (principalDetails == null) {
            // TODO - admin auth 해야함
            return "main";
        }
        model.addAttribute("landmarks",
                landmarkService.getLandmarks(-1)
        );
        return "admin_landmarks";
    }

}
