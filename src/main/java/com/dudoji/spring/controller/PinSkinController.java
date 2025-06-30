package com.dudoji.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PinSkinController {

    @GetMapping("/api/user/pin-skins")
    public void getPinSkins() {

    }

    @GetMapping("/api/user/pin-skins/mine")
    public void getMinePinSkins() {

    }

    @PostMapping("/api/admin/pin-skins")
    public void addPinSkin() {

    }

    @DeleteMapping("/api/admin/pin-skins")
    public void deletePinSkin() {

    }
}
