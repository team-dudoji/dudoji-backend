package com.dudoji.spring.models.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class KakaoUser extends User{

    public KakaoUser(Long id, String name, String email, Timestamp createAt, Long kakaoId) {
        super(id, name, email, createAt);
        this.kakaoId = kakaoId;
    }

    private long kakaoId;
}
