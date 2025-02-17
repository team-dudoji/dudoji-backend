package com.dudoji.spring.controller;

import com.dudoji.spring.dto.RevealCirclesRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/reveal_circles")
public class RevealCircleController {

    @PostMapping("/save")
    public ResponseEntity<String> saveRevealCircles(@RequestBody RevealCirclesRequestDto positionsDto){
        //TODO - have to call MapSectionService().applyRevealCircle()
        return ResponseEntity.ok("saved Successfully");
    }
}
