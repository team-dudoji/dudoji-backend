package com.dudoji.spring.controller;

import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.User;
import com.dudoji.spring.models.domain.UserStep;
import com.dudoji.spring.service.FriendService;
import com.dudoji.spring.service.UserInfoService;
import com.dudoji.spring.service.UserStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/")
    public ResponseEntity<List<User>> getFriends(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<User> result = friendService.getFriendsById(principalDetails.getUid());
        log.info("getFriendsById({})", result);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<String> deleteFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long friendId
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (friendService.deleteFriend(principalDetails.getUid(), friendId)) {
            return ResponseEntity.status(HttpStatus.OK).body("Delete Success");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Delete Failed");
        }
    }

    @PostMapping("/{friendId}")
    public ResponseEntity<String> createFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long friendId
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (friendService.createFriendById(principalDetails.getUid(), friendId)
            && friendService.rejectFriend(principalDetails.getUid(), friendId)) {
            return ResponseEntity.status(HttpStatus.OK).body("Create Success");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Create Failed");
        }
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<User>> getRecommendedFriends(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam String email // TODO: Query? OR Path Variable?
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<User> recommendedUsers = friendService.getRecommendedFriends(email);
        return ResponseEntity.ok(recommendedUsers);
    }

    /*
    FRIEND REQUEST END POINT
     */
    @GetMapping("/requests")
    public ResponseEntity<List<User>> getRequestFriends(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<User> friendRequestList = friendService.getFriendRequestList(principalDetails.getUid());
        return ResponseEntity.ok(friendRequestList);
    }

    @DeleteMapping("/requests/{senderId}")
    public ResponseEntity<Boolean> rejectFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long senderId
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (friendService.rejectFriend(senderId, principalDetails.getUid())) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/requests/{receiverId}")
    public ResponseEntity<Boolean> requestFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long receiverId
    ){
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (friendService.requestFriend(principalDetails.getUid(), receiverId)) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}
