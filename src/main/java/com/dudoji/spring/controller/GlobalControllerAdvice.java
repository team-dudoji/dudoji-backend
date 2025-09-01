package com.dudoji.spring.controller;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${server.port}")
    private int port;

    @Value("${server.url}")
    private String url;

    @ModelAttribute("baseUrl")
    public String baseUrl() {
        return url + ":" + port;
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        log.error("[Error] {}", e.getMessage(), e);
    }
}
