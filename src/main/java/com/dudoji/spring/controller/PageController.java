package com.dudoji.spring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Deprecated
@Controller
@RequestMapping("/login")
public class PageController {
//
//    @Value("${kakao.auth.client_id}")
//    private String client_id;
//    @Value("${kakao.auth.redirect_uri}")
//    private String redirect_uri;
//
    @GetMapping("/page")
    public String loginPage() {
        return "login";
    }
}
