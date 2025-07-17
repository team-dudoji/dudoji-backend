package com.dudoji.spring.controller;

import com.dudoji.spring.service.LandmarkService;
import com.dudoji.spring.service.skin.CharacterSkinService;
import com.dudoji.spring.service.skin.PinSkinService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('admin')")
@RequiredArgsConstructor
public class AdminPageController {

    private final PinSkinService pinSkinService;
    private final CharacterSkinService characterSkinService;
    private final LandmarkService landmarkService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/pin-skins")
    public String getAdminPinSkinPage(
            Model model
    ) {
        model.addAttribute("pinSkins",
                pinSkinService.getPinSkins(-1)
        );
        model.addAttribute("uploadDir", uploadDir);
        return "admin_pinskins";
    }

    
    @GetMapping("/character-skins")
    public String getAdminCharacterSkinPage(
            Model model
    ) {
        model.addAttribute("characterSkins",
                characterSkinService.getCharacterSkins(-1)
        );
        model.addAttribute("uploadDir", uploadDir);
        return "admin_characterskins";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/landmarks")
    public String getAdminLandmarkPage(
            Model model
    ) {
        model.addAttribute("characterSkins",
                landmarkService.getLandmarks(-1)
        );
        model.addAttribute("uploadDir", uploadDir);
        return "admin_landmarks";
    }
}
