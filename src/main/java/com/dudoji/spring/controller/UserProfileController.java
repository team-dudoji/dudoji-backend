package com.dudoji.spring.controller;

import com.dudoji.spring.dto.user.UserProfileDto;
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

    @GetMapping("/mine/coin")
    public ResponseEntity<Integer> getUserCoin(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(
                userInfoService.getCoin(principalDetails.getUid())
        );
    }

    /**
     * Helper method to fetch a single String value for the current user
     * @param principal JWT Token
     * @param fetcher getter func
     * @return HTTP 200 + Real Data
     */
    private ResponseEntity<String> fetchForCurrentUser(
            @AuthenticationPrincipal PrincipalDetails principal,
            Function<Long, String> fetcher
    ) {
        String result = fetcher.apply(principal.getUid());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /**
     * Retrieve the profile image of the authenticated user.
     * @param principal JWT token
     * @return User's profile image url
     */
    @GetMapping("/mine/profile-image")
    public ResponseEntity<String> getProfileImage(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        return fetchForCurrentUser(principal, userInfoService::getProfileImage);
    }

    /**
     * Returns the username of the authenticated user
     * @param principal JWT token
     * @return Username as String
     */
    @GetMapping("/mine/name")
    public ResponseEntity<String> getName(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        return fetchForCurrentUser(principal, userInfoService::getUsername);
    }

    /**
     * Returns the email
     * @param principal JWT Token
     * @return Email as String
     */
    @GetMapping("/mine/email")
    public ResponseEntity<String> getEmail(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        return fetchForCurrentUser(principal, userInfoService::getEmail);
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
