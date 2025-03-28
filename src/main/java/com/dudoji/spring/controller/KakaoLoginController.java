package com.dudoji.spring.controller;

import com.dudoji.spring.dto.KakaoUserInfoResponseDto;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.User;
import com.dudoji.spring.service.KakaoService;
import com.dudoji.spring.service.UserSessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login/kakao")
public class KakaoLoginController {

//    private final KakaoService kakaoService;
    private final UserDao userDao;
    private final UserSessionService userSessionService;
    private String accessToken;

    @Deprecated
    @GetMapping("/callback")
    public ResponseEntity<String> callback() {
//        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
//
//        User user = userDao.getUserByName(userInfo.kakaoAccount.getName());
//        if (user == null) {
//            userDao.createUserWithKakaoId(userInfo.kakaoAccount.profile.getNickName(), userInfo.kakaoAccount.getEmail(), userInfo.getId());
//        }
//        else {
//            // 기존 사용자 업데이트
//        }
//
//        userSessionService.setUser(user);
//        log.info("세션에 사용자 정보 저장: {}", user);
        log.info("=== callback entry ===");


        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @Deprecated
    @GetMapping("/get_token")
    public ResponseEntity<Void> getToken(@RequestParam("token") String token) {
        this.accessToken = token;
        URI uri = URI.create("http://localhost:8000/auth/login/kakao/callback");

        log.info(token);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(uri)
                .build();
    }

    @Deprecated
    @GetMapping("/test_make_token")
    public ResponseEntity<Void> makeToken(@RequestParam("code") String code) {
//        String token = kakaoService.getAccessTokenFromKakao(code);
        String token = "Trash";
        URI uri = URI.create("http://localhost:8000/auth/login/kakao/get_token?token=" + token);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(uri)
                .build();
    }

    // TODO: TEST CODE
    @GetMapping("/session_check")
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);
            response.put(attributeName, attributeValue);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
