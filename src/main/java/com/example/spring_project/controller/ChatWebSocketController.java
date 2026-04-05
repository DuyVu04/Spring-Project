package com.example.spring_project.controller;

import com.example.spring_project.dto.request.ChatMessageRequest;
import com.example.spring_project.dto.response.ChatMessageWSResponse;
import com.example.spring_project.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessageSendingOperations messagingTemplate;


    /**
     * Handle incoming chat messages
     * Destination: /app/chat.sendMessage
     * Broadcasts to: /topic/room.{roomId}
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        log.info("Received message for room: {}", request.getRoomId());

        String username = principal.getName();
        log.debug("Message from user: {}", username);


        try {
            ChatMessageWSResponse response = chatMessageService.sendMessage(request, username);

            String destination = "/topic/room." + request.getRoomId();
            log.debug("Broadcasting message to: {}", destination);

            messagingTemplate.convertAndSend(destination, response);

        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/errors",
                    "Failed to send message: " + e.getMessage()
            );
        }
    }

    /**
     * Handle typing indicators
     * Destination: /app/chat.typing
     * Broadcasts to: /topic/room.{roomId}/typing
     */
    @MessageMapping("/chat.typing")
    public void sendTyping(@Payload ChatMessageRequest request, Principal principal) {
        log.debug("Typing indicator for room: {}", request.getRoomId());

        String username = principal.getName();

        java.util.Map<String, Object> typingNotification = java.util.Map.of(
                "type", "TYPING",
                "roomId", request.getRoomId(),
                "username", username
        );

        String destination = "/topic/room." + request.getRoomId() + "/typing";
        messagingTemplate.convertAndSend(destination, typingNotification);
    }

    /**
     * Join a chat room - sends confirmation to user
     * Destination: /app/chat.join.{roomId}
     */
    @MessageMapping("/chat.join.{roomId}")
    @SendToUser("/queue/join-result")
    public String joinRoom(@DestinationVariable Long roomId) {
        log.info("User joined room: {}", roomId);
        return "Joined room: " + roomId;
    }
}
