package com.dudoji.spring.dto.date;

import lombok.Data;

import java.time.LocalDate;

/**
 * This Class For Manage Duration Date Value
 */
@Data
public class DateRangeRequestDto {

    private LocalDate startDate;
    private LocalDate endDate;
}
