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
    private Integer coin;

    public boolean checkEnoughCoin(int requiredCoin) {
        return coin >= requiredCoin;
    }

    public void useCoin(int usedCoin) {
        if (!checkEnoughCoin(usedCoin)) {
            throw new IllegalArgumentException("Not Enough Coins");
        }

        coin -= usedCoin;
    }
}
