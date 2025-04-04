package com.dudoji.spring.models.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
/**
 * For Control And Store UserStep Info
 */
public class UserStep {

    private Long stepId;
    private Long uid;
    private LocalDate stepDate;
    private int stepMeter;
}
