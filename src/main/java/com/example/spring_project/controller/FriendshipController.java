package com.example.spring_project.controller;

import com.example.spring_project.dto.request.FriendshipRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.FriendshipResponse;
import com.example.spring_project.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping("/request")
    public ApiResponse<FriendshipResponse> sendFriendRequest(
            @RequestBody FriendshipRequest request,
            Principal principal
    ) {
        return ApiResponse.<FriendshipResponse>builder()
                .result(friendshipService.sendFriendRequest(request, principal.getName()))
                .build();
    }

    @PostMapping("/respond/{id}")
    public ApiResponse<FriendshipResponse> respondToRequest(
            @PathVariable Long id,
            @RequestParam String status,
            Principal principal
    ) {
        return ApiResponse.<FriendshipResponse>builder()
                .result(friendshipService.respondToRequest(id, status, principal.getName()))
                .build();
    }

    @GetMapping
    public ApiResponse<List<FriendshipResponse>> getFriends(Principal principal) {
        return ApiResponse.<List<FriendshipResponse>>builder()
                .result(friendshipService.getFriends(principal.getName()))
                .build();
    }

    @GetMapping("/pending")
    public ApiResponse<List<FriendshipResponse>> getPendingRequests(Principal principal) {
        return ApiResponse.<List<FriendshipResponse>>builder()
                .result(friendshipService.getPendingRequests(principal.getName()))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> unfriend(@PathVariable Long id, Principal principal) {
        friendshipService.unfriend(id, principal.getName());
        return ApiResponse.<Void>builder().build();
    }
}
