package com.dudoji.spring.controller;

import com.dudoji.spring.dto.KakaoUserInfoResponseDto;
import com.dudoji.spring.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code){
        String accessToken = kakaoService.getAccessTokenFromKakao(code); // GET ACCESS TOKEN
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
