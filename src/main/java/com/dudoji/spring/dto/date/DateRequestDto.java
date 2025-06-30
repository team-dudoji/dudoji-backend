package com.dudoji.spring.dto.date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * This Class For Translate Json Body Object to Date
 */
@Data
public class DateRequestDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate step_date;
}
