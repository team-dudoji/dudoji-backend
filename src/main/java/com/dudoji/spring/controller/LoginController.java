package com.dudoji.spring.controller;

import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class LoginController {

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }
}
