package com.example.spring_project.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {

    @Value("${jwt.refreshable-duration}")
    private long refreshableDuration;

    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshableDuration);
        response.addCookie(cookie);
    }

    public void setUserRoleCookie(HttpServletResponse response, String role) {
        Cookie cookie = new Cookie("userRole", role);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshableDuration);
        response.addCookie(cookie);
    }

    public void clearCookies(HttpServletResponse response) {
        Cookie accessToken = new Cookie("accessToken", "");
        accessToken.setPath("/");
        accessToken.setHttpOnly(true);
        accessToken.setSecure(false);
        accessToken.setMaxAge(0);
        response.addCookie(accessToken);

        Cookie userRole = new Cookie("userRole", "");
        userRole.setPath("/");
        userRole.setHttpOnly(false);
        userRole.setSecure(false);
        userRole.setMaxAge(0);
        response.addCookie(userRole);
    }

    /**
     * Extract JWT token from cookie string (for WebSocket connections)
     */
    public String extractTokenFromCookie(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return null;
        }

        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] parts = cookie.trim().split("=");
            if (parts.length == 2 && "accessToken".equals(parts[0])) {
                return parts[1];
            }
        }

        return null;
    }
}
