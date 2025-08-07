package com.dudoji.spring.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Order(1)
@Configuration
@ConfigurationProperties(prefix = "app.security.user-agent-filter")
@Setter
@Slf4j
public class UserAgentFilter extends OncePerRequestFilter {

	private List<String> excludeUrls = new ArrayList<>();
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	/**
	 * Dudoji 가 아닌 User-Agent 를 필터링 합니다.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
			String userAgent = request.getHeader("User-Agent");
			log.trace("[UserAgentFilter] value: {}", userAgent);
			log.trace("[UserAgentFilter] excludeUrls: {}", excludeUrls);

			// 어드민 페이지로 접속하는 애들은 두도지 요청이 아니어도 됨.
			// 두도지 아니어도.
			if (userAgent != null && userAgent.contains("Dudoji")) {
				log.trace("[UserAgentFilter] starting do filter");
				filterChain.doFilter(request, response);
				return;
			}
			else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "User-Agent is not supported");
			}
		return;
	}


	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String currentPath = request.getRequestURI();
		return excludeUrls.stream().anyMatch(pattern -> antPathMatcher.match(pattern, currentPath));
	}
}
