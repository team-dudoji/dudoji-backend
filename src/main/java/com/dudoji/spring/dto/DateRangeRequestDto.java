package com.dudoji.spring.dto;

import lombok.Data;

/**
 * This Class For Manage Duration Date Value
 */
@Data
public class DateRangeRequestDto {

    private DateRequestDto startDate;
    private DateRequestDto endDate;
}
