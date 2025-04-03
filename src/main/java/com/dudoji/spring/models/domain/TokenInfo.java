package com.dudoji.spring.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * TokenInfo Class
 * A data class used for generating JWT.
 * Contains grantType and accessToken fields.
 */
@Data
@Builder
@AllArgsConstructor
public class TokenInfo {

    private String grantType;
    private String accessToken;
//    private String refreshToken;
}
