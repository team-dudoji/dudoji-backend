package com.dudoji.spring.models.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Deprecated
@Getter
@Setter
public class KakaoUser extends User{

    private long kakaoId;
}
