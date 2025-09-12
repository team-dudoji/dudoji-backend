package com.dudoji.spring.dto.festival;

import java.time.LocalDate;

import com.dudoji.spring.models.domain.Festival;

public record FestivalResponseDto(
        String name,
        String location,
        LocalDate startDate,
        LocalDate endDate
) {
    public FestivalResponseDto(Festival festival) {
        this(
                festival.getName(),
                festival.getLocation(),
                festival.getStartDate(),
                festival.getEndDate()
        );
    }
}
