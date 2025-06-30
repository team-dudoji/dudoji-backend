package com.dudoji.spring.controller;

import com.dudoji.spring.dto.kakao.KakaoUserInfoResponseDto;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.JwtProvider;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.TokenInfo;
import com.dudoji.spring.models.domain.User;
import com.dudoji.spring.service.KakaoService;
import com.dudoji.spring.service.UserSessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login/kakao")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final UserDao userDao;
    private final UserSessionService userSessionService;
    private final JwtProvider jwtProvider;
    private String accessToken;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/app-login")
    public ResponseEntity<?> applicationKakaoLogin(@RequestHeader("Authorization") String token) {
        // TODO:
        // access token 이 어떤 식으로 날라오는 지 고밍

        log.info("Kakao login token: " + token);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(token);
        User user = userDao.getUserByEmail(userInfo.kakaoAccount.email);

        if (user == null) {
            String password = passwordEncoder.encode(UUID.randomUUID().toString());
            user = User.builder()
                    .name(userInfo.getKakaoAccount().getProfile().getNickName())
                    .password(password)
                    .email(userInfo.kakaoAccount.email)
                    .role("user")
                    .profileImageUrl(userInfo.kakaoAccount.profile.profileImageUrl)
                    .build();
            userDao.createUserByUser(user);
        }
        else {
            log.info("Kakao account already exists");
        }
        user = userDao.getUserByEmail(userInfo.kakaoAccount.email);
        PrincipalDetails principal = new PrincipalDetails(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities());

        TokenInfo tokenInfo = jwtProvider.createToken(authentication);
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


    // TODO: 테스트 코드, 지울 것
    @GetMapping("/give-me-JWT")

    public ResponseEntity<String> getJWT(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principalDetails,
                null,
                principalDetails.getAuthorities()
        );
        TokenInfo tokenInfo = jwtProvider.createToken(authentication);
        String accessToken = tokenInfo.getAccessToken();
        return ResponseEntity.ok(accessToken);
    }
}
