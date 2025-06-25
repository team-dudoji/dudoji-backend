package com.dudoji.spring.models.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
/**
 * For Control And Store UserWalkDistanceDto Info
 */
public class UserWalkDistance {

    private Long stepId;
    private Long uid;
    private LocalDate date;
    private int meters;
}
