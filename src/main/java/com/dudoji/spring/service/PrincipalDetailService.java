package com.dudoji.spring.service;

import com.dudoji.spring.models.dao.UserDao;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("=== [PrincipalDetailService] loadUserByUsername ===");
        log.info("=== [PrincipalDetailService] username = {}", username);
        User user = userDao.getUserByName(username);
        if (user != null) {
            log.info("=== [PrincipalDetailService] User found!");
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
            log.info("=== [PrincipalDetailService] END ===");
            return new PrincipalDetails(user);
        }
        log.info("=== [PrincipalDetailService] user not found");
        log.info("=== [PrincipalDetailService] END ===");
        throw new UsernameNotFoundException(username);
    }
}
