package com.dudoji.spring.controller;

import com.dudoji.spring.dto.skin.PinSkinDto;
import com.dudoji.spring.service.ItemService;
import com.dudoji.spring.service.LandmarkService;
import com.dudoji.spring.service.NpcService;
import com.dudoji.spring.service.skin.CharacterSkinService;
import com.dudoji.spring.service.skin.PinSkinService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('admin')")
@RequiredArgsConstructor
public class AdminPageController {

    private final PinSkinService pinSkinService;
    private final CharacterSkinService characterSkinService;
    private final LandmarkService landmarkService;
    private final ItemService itemService;
    private final NpcService npcService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/pin-skins")
    public String getAdminPinSkinPage(
        @PageableDefault(size = 20, sort = "skinId", direction = Sort.Direction.ASC) Pageable pageable,
        Model model
    ) {
        Page<PinSkinDto> pinSkinDtoPage = pinSkinService.getPinSkinsPage(-1, pageable);
        model.addAttribute("pinSkins",
            pinSkinDtoPage.getContent()
        );
        model.addAttribute("page", pinSkinDtoPage);
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

    @GetMapping("/landmarks")
    public String getAdminLandmarkPage(
            Model model
    ) {
        model.addAttribute("landmarks",
                landmarkService.getLandmarks(-1)
        );
        model.addAttribute("uploadDir", uploadDir);
        return "admin_landmarks";
    }

    @GetMapping("/items")
    public String getAdminItemPage(
        Model model
    ) {
        model.addAttribute("items", itemService.getAllItem());
        model.addAttribute("uploadDir", uploadDir);
        return "admin_items";
    }

    @GetMapping("/npcs")
    public String getAdminNpcPage(
        Model model
    ) {
        return "admin_npcs";
    }

    @GetMapping("/npc-quest")
    public String getAdminNpcQuestPage(
        @RequestParam("npcId") long npcId,
        Model model
    ) {
        model.addAttribute("npc", npcService.getNpcById(npcId));
        return "admin_npc_quest";
    }

    @GetMapping("/npc-skins")
    public String getAdminNpcSkinsPage(
        Model model
    ) {
        model.addAttribute("npcSkins", npcService.getAllNpcSkins());
        model.addAttribute("uploadDir", uploadDir);
        return "admin_npc_skins";
    }
}
