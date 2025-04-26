package com.dudoji.spring.controller;

import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserInfoController
 * REST controller that exposes endpoints for user information operations
 * <ul>
 *     <li>Retrieve authenticated user's profile image</li>
 * </ul>
 */
@RestController
@RequestMapping("api/user/info")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * Retrieve the profile image of the authenticated user.
     * @param principal JWT token
     * @return User's profile image url
     */
    @GetMapping("/get/profile_image")
    public ResponseEntity<String> getProfileImage(
        @AuthenticationPrincipal PrincipalDetails principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        String result = userInfoService.getProfileImage(principal.getUid());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
