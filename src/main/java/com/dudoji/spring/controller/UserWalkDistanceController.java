package com.dudoji.spring.controller;

import com.dudoji.spring.dto.DateRangeRequestDto;
import com.dudoji.spring.dto.DateRequestDto;
import com.dudoji.spring.dto.UserWalkDistancesDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.UserWalkDistance;
import com.dudoji.spring.service.UserWalkDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/walk-distances")
public class UserWalkDistanceController {

    @Autowired
    private UserWalkDistanceService userWalkDistanceService;

    /**
     * Save User Step Information.
     * @param principal JWT
     * @param userWalkDistanceDto It Contains User Distance
     * @return Success - Send Ok
     */
    @PostMapping("")
    public ResponseEntity<String> saveUserWalkDistance(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody UserWalkDistancesDto.UserWalkDistanceDto userWalkDistanceDto) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        if (userWalkDistanceService.applyUserWalkDistance(principal.getUid(), userWalkDistanceDto.getDate(), userWalkDistanceDto.getDistance()))
            return ResponseEntity.status(HttpStatus.CREATED).body("Saved");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Not Saved");
    }

    /**
     * Get UserSteps by Duration And User ID
     * @param principal JWT
     * @param targetDuration It Contains StarDate EndDate
     * @return String That Contains Value Of UserSteps
     */
    @GetMapping("")
    public ResponseEntity<UserWalkDistancesDto> getUserSteps(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody DateRangeRequestDto targetDuration
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        UserWalkDistancesDto result = userWalkDistanceService.getUserWalkDistanceByIdAndDuration(principal.getUid(), targetDuration.getStartDate(), targetDuration.getEndDate());
        return ResponseEntity.ok(result);
    }
}
