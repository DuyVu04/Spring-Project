package com.example.spring_project.configuration;

import com.example.spring_project.service.CookieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompChannelInterceptor implements ChannelInterceptor {

    private final CookieService cookieService;
    private final CustomJwtDecoder customJwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        log.debug("STOMP command: {}", command);

        if (StompCommand.CONNECT == command) {
            log.info("Processing WebSocket CONNECT request");


            String token = extractTokenFromCookie(accessor);

            if (token == null || token.isBlank()) {
                log.error("No JWT token found in cookies. Rejecting connection.");
                throw new RuntimeException("Authentication required: No JWT token provided");
            }

            try {

                Authentication authentication = customJwtDecoder.decodeAndAuthenticate(token);

                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(authentication);
                    log.info("WebSocket authentication successful for user: {}", authentication.getName());
                } else {
                    log.error("Invalid JWT token. Rejecting connection.");
                    throw new RuntimeException("Authentication failed: Invalid JWT token");
                }
            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage());
                throw new RuntimeException("Authentication failed: " + e.getMessage());
            }
        } else if (StompCommand.DISCONNECT == command) {
            log.info("Client disconnecting. Clearing security context.");
            SecurityContextHolder.clearContext();
        }

        return message;
    }


    private String extractTokenFromCookie(StompHeaderAccessor accessor) {

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.containsKey("accessToken")) {
            log.debug("Found accessToken in session attributes");
            return (String) sessionAttributes.get("accessToken");
        }

        log.warn("No accessToken found in session attributes");
        return null;
    }
}
