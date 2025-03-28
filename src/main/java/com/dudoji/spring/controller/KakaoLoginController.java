package com.dudoji.spring.controller;

import com.dudoji.spring.dto.KakaoUserInfoResponseDto;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.JwtTokenProvider;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.TokenInfo;
import com.dudoji.spring.models.domain.User;
import com.dudoji.spring.service.KakaoService;
import com.dudoji.spring.service.UserSessionService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login/kakao")
public class KakaoLoginController {

//    private final KakaoService kakaoService;
    private final UserDao userDao;
    private final UserSessionService userSessionService;
    private final JwtTokenProvider jwtTokenProvider;
    private String accessToken;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/app-login")
    public ResponseEntity<?> applicationKakaoLogin(@RequestHeader("Authorization") String token) {
        // TODO:
        // access token 이 어떤 식으로 날라오는 지 고밍

        log.info("Kakao login token: " + token);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(token);
        User user = userDao.getUserByName(userInfo.getKakaoAccount().getProfile().getNickName());

        if (user == null) {
            String password = passwordEncoder.encode(UUID.randomUUID().toString());
            user = User.builder()
                    .name(userInfo.getKakaoAccount().getProfile().getNickName())
                    .password(password)
                    .email(userInfo.kakaoAccount.email)
                    .role("user")
                    .build();
            userDao.createUserByUser(user);
        }
        else {
            log.info("Kakao account already exists");
        }

        PrincipalDetails principal = new PrincipalDetails(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities());

        TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);
        return ResponseEntity.ok(Map.of("token", tokenInfo));
    }

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
