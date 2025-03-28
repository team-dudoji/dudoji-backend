package com.dudoji.spring.controller;

import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.util.SecurityUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Optional;

@Slf4j
@Controller
public class MainController {

    @GetMapping(value = {"", "/"})
    public String mainPage() {
        log.info("=== Main Page ===");

        return "main";
    }

    @GetMapping("/api1")
    public void api1Page() {
    }

    @GetMapping("/api2")
    public void api2Page() {
    }
}
