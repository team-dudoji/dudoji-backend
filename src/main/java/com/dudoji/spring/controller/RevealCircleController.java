package com.dudoji.spring.controller;

import com.dudoji.spring.dto.RevealCirclesRequestDto;
import com.dudoji.spring.models.dao.MapSectionDao;
import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Point;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.MapSectionService;
import com.dudoji.spring.util.MapSectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user/reveal-circles")
public class RevealCircleController {

    @Autowired
    private MapSectionService mapSectionService;

    @PostMapping("")
    public ResponseEntity<String> saveRevealCircles(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody RevealCirclesRequestDto positionsDto){
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        positionsDto.getRevealCircles().forEach(revealCircle -> {
            Point targetPoint = Point.fromGeographic(revealCircle.getLng(), revealCircle.getLat());
            mapSectionService.applyRevealCircle(principal.getUid(), targetPoint, revealCircle.getRadius());
        });

        return ResponseEntity.ok("saved Successfully " + positionsDto.toString());
    }
}
