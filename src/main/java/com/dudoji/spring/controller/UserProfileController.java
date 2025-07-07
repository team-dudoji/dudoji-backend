package com.dudoji.spring.controller;

import com.dudoji.spring.dto.UserProfileDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.function.Function;

@RestController
@RequestMapping("/api/user/profiles")
@PreAuthorize("isAuthenticated()")
public class UserProfileController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/mine")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(
                userInfoService.getUserProfileById(principalDetails.getUid())
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @PathVariable("userId") Long userId
    ) {
        return ResponseEntity.ok(
                userInfoService.getUserProfileById(userId)
        );
    }
}
