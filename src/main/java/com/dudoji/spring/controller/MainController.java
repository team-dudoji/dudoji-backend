package com.dudoji.spring.controller;

import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.User;
import com.dudoji.spring.util.SecurityUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Map;
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
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal PrincipalDetails principal,
    @RequestBody String sexyguy) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증정보 x");
        }
        Map<String, Object> result = Map.of(
                "username", principal.getName(),
                "userId", principal.getUid(),
                "user????", principal.getPassword(),
                "sexy guy", sexyguy
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api2")
    public void api2Page() {
    }
}
