package com.dudoji.spring.config;

import com.dudoji.spring.models.domain.JwtProvider;
import com.dudoji.spring.security.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtProvider jwtProvider) throws Exception {

        http
                .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
//                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/auth/login/kakao/**", "/oauth2/**").permitAll()
//                        .requestMatchers("/api1/**").hasRole("user")
//                        .requestMatchers("/api2/**").hasRole("admin")
                        .anyRequest().authenticated()
        )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                // For Login Part
                .formLogin((formLogin) ->
                        formLogin
                                .loginPage("/user/loginForm")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/")
                                .failureUrl("/user/loginForm?error=true")
                                .permitAll()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login/page")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService)
                        )
                )
                .csrf(csrf -> csrf.disable())
        ;
        // 로그아웃 부분도 만들어야 함.
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder passwordEncoder
    ) throws Exception {

        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }
}
