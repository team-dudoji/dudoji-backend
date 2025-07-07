package com.dudoji.spring.models.domain;


import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long id;
    private String password;
    private String role;
    private String name;
    private String email;
    private Date createAt;
    private String provider;
    private String providerId;
    private String profileImageUrl;
}
