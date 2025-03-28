package com.dudoji.spring.models.domain;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String secretKey;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public JwtTokenProvider() {
        Dotenv dotenv = Dotenv.load();
        this.secretKey = dotenv.get("JWT_SECRET_KEY");

        if (this.secretKey == null) {
            throw new RuntimeException("JWT_SECRET not set");
        }
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public TokenInfo createToken(Authentication authentication) {

        // authentication.getAuthorities() -> Get Authorities of User
        // .stream() list -> stream
        // map -> GrantedAuthority 객체에서 문자열만 추춘
        // collect -> , 로 이어 붙임.
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toUpperCase)
                .collect(Collectors.joining(","));
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        String jwt = Jwts.builder()
                .subject(authentication.getName())
                .claim("auth", authorities)
                .claim("userId", ((PrincipalDetails) authentication.getPrincipal()).getUid())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();

        // All JWT Request has prefix "Bearer"
        return new TokenInfo("Bearer ", jwt);
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public Authentication getAuthentication(String jwt) {
        Claims claims = getClaims(jwt);

        String auth = Optional.ofNullable(claims.get("auth", String.class))
                .orElseThrow(() -> new RuntimeException("JWT_AUTH required"));

        Long userId = Optional.ofNullable(claims.get("userId", Long.class))
                .orElseThrow(() -> new RuntimeException("User ID required"));

        Collection<GrantedAuthority> authorities = Arrays.stream(auth.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User user = User.builder()
                .id(userId)
                .name(claims.getSubject())
                .password("")
                .role("user")
                .build();

        PrincipalDetails principal= new PrincipalDetails(user);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            // 무슨 오류인 지는 알 필요가 없지 않을 까용!
            log.debug("=== [JWT validation error] ==={}", e.getMessage());
            return false;
        }

    }
}
