package com.example.spring_project.controller;

import com.example.spring_project.dto.request.UserRequest;
import com.example.spring_project.dto.request.UserUpdateRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.UserResponse;
import com.example.spring_project.entity.User;
import com.example.spring_project.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor // Sử dụng @RequiredArgsConstructor để tự động tạo constructor với các trường final thay cho @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)

public class UserController {

    UserService userService;

    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserRequest userRequest) {
        ApiResponse <User> response = new ApiResponse<>();
        response.setResult(userService.createUser(userRequest));
        return response;
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getAllUser(){

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}",authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));


        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<User> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        ApiResponse <User> response = new ApiResponse<>();
        User user =userService.getUser(userId);
        response.setResult(userService.updateUser(userId, userUpdateRequest));
        return response;
    }




    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }


    @GetMapping("/{userId}")
    ApiResponse<User> getUserId(@PathVariable String userId){
        ApiResponse<User> response =new ApiResponse<>();
        User user = userService.getUser(userId);
        response.setResult(user);
        return response;
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        User user =userService.getUser(userId);
        ApiResponse<String> response = new ApiResponse<>();
        userService.deleteUser(userId);
        response.setMessage("User with id " + userId + " deleted successfully.");
        return response;
    }

}
