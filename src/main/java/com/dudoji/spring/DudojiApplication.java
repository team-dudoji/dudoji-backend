package com.dudoji.spring;


import com.dudoji.spring.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class DudojiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DudojiApplication.class, args);
    }
}
