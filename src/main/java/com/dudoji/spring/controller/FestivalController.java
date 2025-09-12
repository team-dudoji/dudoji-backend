package com.dudoji.spring.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dudoji.spring.dto.festival.FestivalResponseDto;
import com.dudoji.spring.service.FestivalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/festivals")
public class FestivalController {

    private final FestivalService festivalService;

    @GetMapping("/today")
    public ResponseEntity<List<FestivalResponseDto>> getTodayFestivals() {
        var result = festivalService.getTodayFestival();
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<FestivalResponseDto>> getFestivals(
            @RequestParam("date") LocalDate date
    ) {
        var result = festivalService.getFestivalByDate(date);
        return ResponseEntity.ok(result);
    }
}
