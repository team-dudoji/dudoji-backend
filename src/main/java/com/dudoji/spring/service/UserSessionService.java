package com.dudoji.spring.service;

import com.dudoji.spring.models.domain.User;

public interface UserSessionService {
    void setUser(User user);
    User getUser();
    void clearUser();
}
