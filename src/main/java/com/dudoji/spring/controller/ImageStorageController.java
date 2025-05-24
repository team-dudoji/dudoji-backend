package com.dudoji.spring.controller;

import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user/images")
public class ImageStorageController {

    @Autowired
    private ImageStorageService imageStorageService;

    @PostMapping("")
    public ResponseEntity<String> uploadImage(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam("image") MultipartFile image
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String imageUrl = imageStorageService.storeImage(image);
        return ResponseEntity.ok().body(imageUrl);
    }
}
