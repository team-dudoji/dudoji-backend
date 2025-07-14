package com.dudoji.spring.util.log;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class RequestLogFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest httpRequest) {
            log.info(
                    "Request: method={}, uri={}, ip={}, userAgent={}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpRequest.getRemoteAddr(),
                    Objects.requireNonNullElse(httpRequest.getHeader(HttpHeaders.USER_AGENT), "Unknown")
            );

            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            log.warn("Received a non-HTTP request. Skipping request logging.");
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
