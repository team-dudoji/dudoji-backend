package com.dudoji.spring.controller;

import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j // For logging
@Controller// Spring MVC Controller
@RequiredArgsConstructor // Make field constructor
@RequestMapping("/user") // For User Join
public class JoinController {

    private final UserDao userDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        log.info("join post approach");

        user.setRole("user");
        String rawPassword = user.getPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        userDao.createUserByUser(user);
        log.info("user = {} ", user);
        return "redirect:/loginForm";
    }
}
