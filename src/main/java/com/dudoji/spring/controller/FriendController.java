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

    @DeleteMapping("/")
    public ResponseEntity<String> deleteFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam long friendId
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

    @PostMapping("/")
    public ResponseEntity<String> createFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam long friendId
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (friendService.createFriendById(principalDetails.getUid(), friendId)) {
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
    @GetMapping("/friend-request")
    public ResponseEntity<List<User>> getRequestFriends(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<User> friendRequestList = friendService.getFriendRequestList(principalDetails.getUid());
        return ResponseEntity.ok(friendRequestList);
    }

    @PostMapping("/friend-request/{sender_id}/accept")
    public ResponseEntity<Boolean> acceptFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long sender_id
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (friendService.acceptFriend(sender_id, principalDetails.getUid())) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/friend-request/{sender_id}/reject")
    public ResponseEntity<Boolean> rejectFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long sender_id
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (friendService.rejectFriend(sender_id, principalDetails.getUid())) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("friend-request/{receiver_id}")
    public ResponseEntity<Boolean> requestFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long receiver_id
    ){
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (friendService.requestFriend(principalDetails.getUid(), receiver_id)) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}
