package com.dudoji.spring.service;

import com.dudoji.spring.models.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class HttpSessionuserSessionService implements UserSessionService {

    private static final String USER_SESSION_KEY = "userSession";
    private final HttpSession userSession;

    public HttpSessionuserSessionService(HttpSession userSession) {
        this.userSession = userSession;
    }
    @Override
    public void setUser(User user) {
        userSession.setAttribute(USER_SESSION_KEY, user);
    }

    @Override
    public User getUser() {
        return (User) userSession.getAttribute(USER_SESSION_KEY);
    }

    @Override
    public void clearUser() {
        userSession.removeAttribute(USER_SESSION_KEY);
    }
}
