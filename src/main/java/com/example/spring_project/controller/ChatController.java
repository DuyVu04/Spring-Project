package com.example.spring_project.controller;


import com.example.spring_project.dto.request.CreateRoomRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.ChatRoomResponse;
import com.example.spring_project.entity.User;
import com.example.spring_project.repository.UserRepository;
import com.example.spring_project.service.ChatMessageService;
import com.example.spring_project.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;


    @PostMapping("/rooms")
    public ApiResponse<ChatRoomResponse> createRoom(
            @RequestBody CreateRoomRequest request,
            Principal principal
    ) {
        String username = principal.getName();
        return ApiResponse.<ChatRoomResponse>builder()
                .result(chatRoomService.createRoom(request, username))
                .build();
    }
    @GetMapping("/rooms")
    public ApiResponse<Object> getRooms(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ApiResponse.builder()
                .result(chatRoomService.getUserRooms(user.getId()))
                .build();
    }
    @GetMapping("/rooms/{roomId}/messages")
    public ApiResponse<Object> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.builder()
                .result(chatMessageService.getMessages(roomId, page, size))
                .build();
    }
}
