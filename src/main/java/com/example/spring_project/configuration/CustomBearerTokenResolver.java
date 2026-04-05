package com.example.spring_project.configuration;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CustomBearerTokenResolver implements BearerTokenResolver {

    private final DefaultBearerTokenResolver defaultBearerTokenResolver;

    public CustomBearerTokenResolver() {
        this.defaultBearerTokenResolver = new DefaultBearerTokenResolver();
    }

    @Override
    public String resolve(HttpServletRequest request) {

        String token = defaultBearerTokenResolver.resolve(request);
        if (token != null) {
            return token;
        }


        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "accessToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}
