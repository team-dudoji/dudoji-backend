package com.dudoji.spring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${server.port}")
    private int port;

    @Value("${server.url")
    private String url;

    @ModelAttribute("baseUrl")
    public String baseUrl() {
        return url + ":" + port;
    }
}
