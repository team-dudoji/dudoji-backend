package com.dudoji.spring.controller;

import com.dudoji.spring.dto.user.UserSimpleDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.FollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/user/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @GetMapping("")
    public ResponseEntity<List<UserSimpleDto>> getUsers(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestParam(value = "type", defaultValue = "FOLLOWING") String type,
        @RequestParam(value = "keyword", defaultValue = "") String keyword,
        @PageableDefault(size = 10, sort = "followingAt", direction = Sort.Direction.ASC)Pageable pageable
    ) {
        List<UserSimpleDto> result = followService.getUsers(principalDetails.getUid(), type, keyword, pageable);
        return ResponseEntity.ok(result);
    }

/*
    @GetMapping("")
    public ResponseEntity<List<UserSimpleDto>> getFollowing(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok(
                followService.getFollowingById(principalDetails.getUid(), limit, offset)
        );
    }
*/

    @GetMapping("/follower")
    public ResponseEntity<List<UserSimpleDto>> getFollowers(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok(
                followService.getFollowerById(principalDetails.getUid(), limit, offset)
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteFollowing(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long userId
    ) {
        if (followService.deleteFollowing(principalDetails.getUid(), userId)) {
            return ResponseEntity.status(HttpStatus.OK).body("Delete Success");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Delete Failed");
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> createFollowing(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long userId
    ) {
        if (followService.createFollowing(principalDetails.getUid(), userId)) {
            return ResponseEntity.status(HttpStatus.OK).body("Create Success");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Create Failed");
        }
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<UserSimpleDto>> getRecommendedFriends(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam String email // TODO: Query? OR Path Variable?
    ) {
        return ResponseEntity.ok(followService.getRecommendedFollow(email));
    }


    /**
     * ----
     * &#064;Deprecated
     */

    /*
    @Deprecated
    @GetMapping("/requests")
    public ResponseEntity<List<User>> getRequestFriends(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<User> friendRequestList = followService.getFriendRequestList(principalDetails.getUid());
        return ResponseEntity.ok(friendRequestList);
    }

    @Deprecated
    @DeleteMapping("/requests/{senderId}")
    public ResponseEntity<Boolean> rejectFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long senderId
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (followService.rejectFriend(senderId, principalDetails.getUid())) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @Deprecated
    @PostMapping("/requests/{receiverId}")
    public ResponseEntity<Boolean> requestFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long receiverId
    ){
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (followService.requestFriend(principalDetails.getUid(), receiverId)) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
    */
}
