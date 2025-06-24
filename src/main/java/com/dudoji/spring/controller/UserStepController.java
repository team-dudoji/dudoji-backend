package com.dudoji.spring.controller;

import com.dudoji.spring.dto.date.DateRangeRequestDto;
import com.dudoji.spring.dto.date.DateRequestDto;
import com.dudoji.spring.dto.step.UserStepsRequestDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.UserStep;
import com.dudoji.spring.service.UserStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/steps")
public class UserStepController {

    @Autowired
    private UserStepService userStepService;

    /**
     * Save User Step Information.
     * @param principal JWT
     * @param userStepDto It Contains User Step
     * @return Success - Send Ok
     */
    @PostMapping("")
    public ResponseEntity<String> saveUserStep(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody UserStepsRequestDto userStepDto) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        userStepDto.getUserSteps().forEach(userStep -> {
            userStepService.applyUserStep(principal.getUid(), userStep.getStep_date(), userStep.getStep_count());
        });

        return ResponseEntity.status(HttpStatus.CREATED).body("Saved");
    }

    /**
     * Get Only One day of User Step
     * @param principal JWT
     * @param targetDate It Contains Date
     * @return String That Contains Value of UserStep
     */
    @GetMapping("")
    public ResponseEntity<?> getUserStep(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody DateRequestDto targetDate
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        UserStep result = userStepService.getUserStepByIdAndDate(principal.getUid(), targetDate.getStep_date());

        //return ResponseEntity.status(HttpStatus.OK).body(result.toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Get UserSteps by Duration And User ID
     * @param principal JWT
     * @param targetDuration It Contains StarDate EndDate
     * @return String That Contains Value Of UserSteps
     */
    @GetMapping("/range")
    public ResponseEntity<?> getUserSteps(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody DateRangeRequestDto targetDuration
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        List<UserStep> result = userStepService.getUserStepsByIdAndDuration(principal.getUid(), targetDuration.getStartDate().getStep_date(), targetDuration.getEndDate().getStep_date());
        return ResponseEntity.ok(result);
    }
}
