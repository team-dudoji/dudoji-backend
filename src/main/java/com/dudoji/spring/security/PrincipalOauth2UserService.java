package com.dudoji.spring.security;

import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
    throws OAuth2AuthenticationException {
        /*
        About Request Information.
        Try to login with it.
         */
        log.info("=== OAuth2UserService: loadUser/Request Information ===");

        // Get Information from Outside Server
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String provider_id = null, email = null, name = null, profileImageUrl = null;
        switch (provider) {
            // TODO: Change
            case "google":
                break;
            case "kakao":
                provider_id = Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                email = (String) kakaoAccount.get("email");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                profileImageUrl = (String) profile.get("profile_image_url");
                if (profile != null) { name = (String) profile.get("nickname"); }
                break;
            case "naver":
                break;
        }

        // Set Random Password
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        // TODO: admin change probability
        String role = "user";
        if (provider_id == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "ProviderId not found");
        }

        User user = userDao.getUserByName(name);

        if (user == null) {
            user = User.builder()
                    .name(name)
                    .email(email)
                    .password(password)
                    // provider는 조정 후
                    .role(role)
                    .profileImageUrl(profileImageUrl)
                    .build();
            userDao.createUserByUser(user);
        }
        else {
            log.info("User already exists");
        }
        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
