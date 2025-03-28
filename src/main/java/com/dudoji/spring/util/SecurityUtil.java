package com.dudoji.spring.util;

import com.dudoji.spring.models.domain.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtil {

    public static Optional<PrincipalDetails> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable((PrincipalDetails) auth.getPrincipal());
    }
}
