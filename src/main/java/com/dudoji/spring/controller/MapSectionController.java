package com.dudoji.spring.controller;

import com.dudoji.spring.dto.MapSectionResponseDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.MapSectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.Media;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user/map_section")
public class MapSectionController {

    @Autowired
    private MapSectionService mapSectionService;
    // 아니지 모든 맵섹션을 나눠준다고 생각하면, 그냥 principal 만 있어도 되잖아
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MapSectionResponseDto> getMapSection(
            @AuthenticationPrincipal PrincipalDetails principal
    ){
        if (principal == null) {
            log.info("=== Error In MapSectionController ===");
            log.info("=== Not Auth ===");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Service 만들고 오기
        MapSectionResponseDto result = mapSectionService.getUserMapSections(principal.getUid());

        return ResponseEntity.ok(result);
    }
}
