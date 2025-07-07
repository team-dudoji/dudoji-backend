package com.dudoji.spring.controller;

import com.dudoji.spring.dto.mission.AchievementDto;
import com.dudoji.spring.dto.mission.QuestDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.mission.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
@RestController
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/quests")
    public ResponseEntity<List<QuestDto>> getQuests(
            @AuthenticationPrincipal PrincipalDetails principalDetails
            ) {
        return ResponseEntity.ok(
                missionService.getQuestProgresses(
                        principalDetails.getUid()
                )
        );
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementDto>> getAchievements(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(
                missionService.getAchievementProgresses(
                        principalDetails.getUid()
                )
        );
    }
}
