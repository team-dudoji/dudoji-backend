package com.dudoji.spring.controller;

import com.dudoji.spring.models.domain.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Slf4j
@Controller
public class MainController {

    @GetMapping(value = {"", "/"})
    public String mainPage() {
        log.info("=== Main Page ===");
        return "main";
    }

    /**
     * JWT Test Code
     * @param principal JWT which user has.
     * @return ok with result value
     */
    @GetMapping("/api1")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        Map<String, Object> result = Map.of(
                "username", principal.getName(),
                "userId", principal.getUid(),
                "user????", principal.getPassword()
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api2")
    public void api2Page() {
    }
}
