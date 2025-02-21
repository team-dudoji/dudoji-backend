package com.dudoji.spring.models.domain;


import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
public class User {

    public User(Long id, String name, String email, Timestamp createAt, int kakaoUserId){
        this.id = id;
        this.name = name;
        this.email = email;
        this.createAt = createAt;
        this.kakaoUserId = kakaoUserId;
    }

    private Long id;
    private String name;
    private String email;
    private Date createAt;
    private int kakaoUserId;
}
