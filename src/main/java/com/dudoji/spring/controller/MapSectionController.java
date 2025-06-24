package com.dudoji.spring.controller;

import com.dudoji.spring.dto.mapsection.MapSectionResponseDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.MapSectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user/map-sections")
public class MapSectionController {

    @Autowired
    private MapSectionService mapSectionService;
    // 아니지 모든 맵섹션을 나눠준다고 생각하면, 그냥 principal 만 있어도 되잖아
    @GetMapping("")
    public ResponseEntity<MapSectionResponseDto> getMapSection(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Service 만들고 오기
        MapSectionResponseDto result = mapSectionService.getUserMapSections(principalDetails.getUid());

        return ResponseEntity.ok(result);
    }
}
