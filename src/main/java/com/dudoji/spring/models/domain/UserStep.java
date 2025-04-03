package com.dudoji.spring.models.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
/**
 * For Control And Store UserStep Info
 */
public class UserStep {

    private Long stepId;
    private Long uid;
    private Date stepDate;
    private int stepCount;
}
