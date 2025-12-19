package com.example.spring_project.controller;

import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.WishlistResponse;
import com.example.spring_project.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    ApiResponse<WishlistResponse> addToWishlist(@RequestParam UUID courseId) {
        return ApiResponse.<WishlistResponse>builder()
                .result(wishlistService.addToWishlist(courseId))
                .build();
    }

    @DeleteMapping("/{courseId}")
    ApiResponse<Void> removeFromWishlist(@PathVariable UUID courseId) {
        wishlistService.removeFromWishlist(courseId);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping
    ApiResponse<List<WishlistResponse>> getMyWishlist() {
        return ApiResponse.<List<WishlistResponse>>builder()
                .result(wishlistService.getMyWishlist())
                .build();
    }
}
