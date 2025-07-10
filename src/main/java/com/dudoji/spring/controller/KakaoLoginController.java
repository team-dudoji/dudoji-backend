package com.dudoji.spring.controller;

import com.dudoji.spring.dto.kakao.KakaoUserInfoResponseDto;
import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.JwtProvider;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.TokenInfo;
import com.dudoji.spring.models.domain.User;
import com.dudoji.spring.service.KakaoService;
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
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/app-login")
    public ResponseEntity<?> applicationKakaoLogin(@RequestHeader("Authorization") String token) {

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

    @GetMapping("/validate")
    public ResponseEntity<String> validateJwt(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Jwt is not valid");
        }
        return ResponseEntity.ok("Jwt is valid");
    }

    @Deprecated
    @GetMapping("/callback")
    public ResponseEntity<String> callback() {
        log.info("=== callback entry ===");

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @Deprecated
    @GetMapping("/get_token")
    public ResponseEntity<Void> getToken(@RequestParam("token") String token) {

        URI uri = URI.create("http://localhost:8000/auth/login/kakao/callback");

        log.info(token);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(uri)
                .build();
    }
}
