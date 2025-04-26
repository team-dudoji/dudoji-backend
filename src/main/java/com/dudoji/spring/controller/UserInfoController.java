package com.dudoji.spring.controller;

import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * UserInfoController
 * REST controller that exposes endpoints for user information operations
 * <ul>
 *     <li>Retrieve authenticated user's profile image</li>
 * </ul>
 */
@RestController
@RequestMapping("api/user/info")
@PreAuthorize("isAuthenticated()")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * Helper method to fetch a single String value for the current user
     * @param principal JWT Token
     * @param fetcher getter func
     * @return HTTP 200 + Real Data
     */
    private ResponseEntity<String> fetchForCurrentUser(
            @AuthenticationPrincipal PrincipalDetails principal,
            java.util.function.Function<Long, String> fetcher
    ) {
        String result = fetcher.apply(principal.getUid());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /**
     * Retrieve the profile image of the authenticated user.
     * @param principal JWT token
     * @return User's profile image url
     */
    @GetMapping("/get/profile-image")
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
    @GetMapping("/get/name")
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
    @GetMapping("/get/email")
    public ResponseEntity<String> getEmail(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        return fetchForCurrentUser(principal, userInfoService::getEmail);
    }
}
