package com.dudoji.spring.controller;

import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.ImageStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@PreAuthorize("isAuthenticated()")
@RestController
public class ImageStorageController {

    @Autowired
    private ImageStorageService imageStorageService;

    @PostMapping("/api/user/images")
    public ResponseEntity<String> uploadImage(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam("image") MultipartFile image
    ) {

        if (image == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String imageUrl = imageStorageService.storeImageToRandomName(image, "memos/"); // TODO: OS 별로 ㄱㅊ은 지 모름
        return ResponseEntity.ok().body(imageUrl);
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/api/admin/images/**")
    public ResponseEntity<String> uploadImage(
            HttpServletRequest request,
            @RequestParam("image") MultipartFile image
    ) {
        String requestURI = request.getRequestURI();
        String prefix = "/api/admin/images/";
        String pathName = requestURI.substring(requestURI.indexOf(prefix) + prefix.length());

        String imageUrl = imageStorageService.storeImageToRandomName(image, pathName + "/");
        return ResponseEntity.ok().body(imageUrl);
    }
}
